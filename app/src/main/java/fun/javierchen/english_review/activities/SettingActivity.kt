package `fun`.javierchen.english_review.activities

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `fun`.javierchen.english_review.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onThemeUpdated: (Boolean) -> Unit,
    onAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 账户设置模块
        ListItem(
            headlineContent = { Text("账户设置") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            trailingContent = {
                Icon(Icons.Default.ArrowForward, null)
            },
            modifier = Modifier.clickable { onAccountClick() }
        )

        HorizontalDivider()

        // 主题设置模块
        ListItem(
            headlineContent = { Text("主题设置") },
            supportingContent = { Text("深色/浅色主题切换") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null
                )
            },
            trailingContent = {
                Switch(
                    checked = darkTheme,
                    onCheckedChange = onThemeUpdated
                )
            }
        )

        HorizontalDivider()

        // 其他设置项
        ListItem(
            headlineContent = { Text("隐私设置") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
            },
            trailingContent = {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        )
    }
}

// 在 Activity 中使用（更新原有 Greeting 函数）
class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SettingsPreview()
            }
        }
    }
}

// 预览组件更新
@Composable
fun SettingsPreview() {
    AppTheme {
        SettingsScreen(
            darkTheme = false,
            onThemeUpdated = {},
            onAccountClick = {}
        )
    }
}
