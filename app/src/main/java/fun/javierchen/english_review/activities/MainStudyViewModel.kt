package `fun`.javierchen.english_review.activities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `fun`.javierchen.english_review.config.ApiKeyConfig
import `fun`.javierchen.english_review.model.Message
import `fun`.javierchen.english_review.model.TranslationExercise
import `fun`.javierchen.english_review.model.request.CompletionRequest
import `fun`.javierchen.english_review.service.DeepSeekApiService
import `fun`.javierchen.english_review.util.HttpSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * 创建AI请求实例
 */
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.deepseek.com")
    .addConverterFactory(GsonConverterFactory.create())
    .client(HttpSingleton.client)
    .build()

class MainStudyViewModel : ViewModel() {
    // 状态管理
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _translateText = MutableStateFlow("")
    val translateText: StateFlow<String> = _translateText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _translationExercise = MutableStateFlow<TranslationExercise?>(null)
    val translationExercise: StateFlow<TranslationExercise?> = _translationExercise.asStateFlow()

    // 创建API服务
    private val apiService = retrofit.create(DeepSeekApiService::class.java)

    fun fetchTranslation() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getTodayTranslate()
                _translateText.value = result
            } catch (e: Exception) {
                _errorMessage.value = when (e) {
                    is IOException -> "网络异常：${e.localizedMessage}"
                    else -> "服务器错误：${e}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getTodayTranslate(): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://192.168.186.206:82/api/team/list")
            .build()

        try {
            HttpSingleton.client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP ${response.code}")
                }
                response.body?.string() ?: throw IOException("Empty response")
            }
        } catch (e: Exception) {
            throw when (e) {
                is IOException -> IOException("网络请求失败: ${e.message}")
                else -> Exception("未知错误: ${e}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateContent(prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val response = apiService.createCompletion(
                    auth = "Bearer ${getApiKey()}", // 从安全存储获取
                    request = CompletionRequest(
                        model = "deepseek-chat",
                        messages = listOf(
                            Message(role = "system", content = "你是一个有经验的英语四级老师，擅长贴合真实考题教学,并会在语料中加入适当的生词来帮助学生"),
                            Message(role = "user", content = prompt)
                        )
                    )
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _translateText.value =
                            response.body()?.choices?.first()?.message?.content ?: ""
                        _translationExercise.value = JSON2TranslationExercise(_translateText.value)
                    } else {
                        _errorMessage.value = "API Error: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = when (e) {
                    is IOException -> "网络错误：${e.message}"
                    else -> "系统错误：${e.localizedMessage}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // todo: 使用更安全的方式传递 key
    // warning: 仅供测试使用，请勿将此方法用于生产环境
    // ! 请不要将您的 API 密钥直接嵌入到代码中，这可能会导致安全风险。
    // 请考虑使用更安全的方式，如环境变量、配置文件等，来存储和传递 API 密钥。
    private fun getApiKey(): String {
        return ApiKeyConfig.API_KEY
    }
}

/**
 * 将 JSON 字符串转换为 TranslationExercise 对象
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun JSON2TranslationExercise(json: String): TranslationExercise {
    return TranslationExercise.build.fromJson(json, TranslationExercise::class.java)
}