package `fun`.javierchen.english_review.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `fun`.javierchen.english_review.R
import `fun`.javierchen.english_review.util.HttpSingleton
import kotlinx.coroutines.launch
import okhttp3.Request
import java.io.IOException

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
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            translateText = getTodayTranslate()
                        } catch (e: Exception) {
                            errorMessage = when (e) {
                                is IOException -> "网络连接异常"
                                else -> "服务器错误：${e.message}"
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ) {
                Text("获取翻译")
            }
        }
    }
}

// 修改后的网络请求函数
suspend fun getTodayTranslate(): String {
    return try {
        HttpSingleton.client.newCall(
            Request.Builder()
                .url("https://profile-avatar.csdnimg.cn/a7e8397ab14b4e07b0ee1e570638f9d3_javierchen_2295.jpg!1")
                .build()
        ).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            println(response)
            response.body?.string() ?: throw IOException("Empty response")
        }
    } catch (e: Exception) {
        throw e // 将异常抛给调用方处理
    }
}
