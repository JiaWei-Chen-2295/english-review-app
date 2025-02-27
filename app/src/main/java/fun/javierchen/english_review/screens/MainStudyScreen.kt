package `fun`.javierchen.english_review.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import `fun`.javierchen.english_review.util.HttpSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import okhttp3.Request

@Composable
fun MainStudyApp() {
    // 状态管理
    var isLoading by remember { mutableStateOf(false) }
    var translateText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                    Text("加载中...")
                }

                errorMessage != null -> {
                    Text("❌ 加载失败", color = Color.Red)
                    Text(errorMessage ?: "")
                }

                else -> {
                    Text("今日翻译", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = translateText.ifEmpty { "点击获取今日翻译" },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        isLoading = true
                        errorMessage = null
                        try {
                            val result = getTodayTranslate()
                            withContext(Dispatchers.Main) {
                                translateText = result
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                errorMessage = when (e) {
                                    is IOException -> "网络异常：${e.localizedMessage}"
                                    else -> "服务器错误：${e.javaClass.simpleName}"
                                }
                            }
                        } finally {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                            }
                        }
                    }
                }
            ) {
                Text("获取翻译")
            }


        }
    }
}

suspend fun getTodayTranslate(): String {
    return withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://192.168.173.206:82/api/team/list")
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
                else -> Exception("未知错误: ${e.message}")
            }
        }
    }
}

