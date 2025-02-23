package `fun`.javierchen.english_review.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import `fun`.javierchen.english_review.common.ThemeManager
import `fun`.javierchen.english_review.components.wrapper.AppWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAccountClick: () -> Unit,
    modifier: Modifier
) {

    // 设置标题
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                modifier = modifier
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
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
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                },
                modifier = Modifier.clickable { onAccountClick() }
            )

            HorizontalDivider()

            // 主题设置模块
            ListItem(
                headlineContent = { Text("主题设置") },
                supportingContent = { Text("选择你想要的主题") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    ThemeModeSegmentedButton()
                }
            )

            HorizontalDivider()

            // 主题颜色设置模块
            ListItem(
                headlineContent = { Text("主题颜色设置") },
                supportingContent = { Text("是否开启动态取色") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    DynamicThemeSegmentedSwitch()
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
}

// 在 Activity 中使用（更新原有 Greeting 函数）
class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppWrapper {
                val context = LocalContext.current
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsScreen(
                        onAccountClick = {
                            // 处理账户修改跳转逻辑
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun ThemeModeSegmentedButton() {
    val context = LocalContext.current
    val currentMode by ThemeManager.themeMode // 使用State解构
    val options = listOf("浅色", "深色", "系统")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                // 修复形状参数
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { ThemeManager.toggleTheme(context, index) },
                selected = index == currentMode,
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun DynamicThemeSegmentedSwitch() {
    val context = LocalContext.current
    val currentMode by ThemeManager.isDynamicTheme
    Switch(
        checked = currentMode,
        onCheckedChange = {
            ThemeManager.toggleDynamicTheme(context, it)
        }
    )
}
