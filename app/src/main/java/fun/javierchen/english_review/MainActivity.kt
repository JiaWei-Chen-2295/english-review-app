package `fun`.javierchen.english_review

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `fun`.javierchen.english_review.activities.LoginActivity
import `fun`.javierchen.english_review.common.LoginManager
import `fun`.javierchen.english_review.common.ThemeManager
import `fun`.javierchen.english_review.components.AppBottomBar
import `fun`.javierchen.english_review.components.wrapper.AppWrapper
import `fun`.javierchen.english_review.model.NavigationItems
import `fun`.javierchen.english_review.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            // 在 IO 线程执行初始化
            withContext(Dispatchers.IO) {
                ThemeManager.initialize(applicationContext)
                LoginManager.initialize(applicationContext)
            }

            // 当完成初始化之后 再进行 UI 更新
            LoginManager.initializationComplete.collect { completed ->
                if (completed) {
                    // 确保在此处获取最新状态
                    if (!LoginManager.isLoggedIn.value) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        setContent { AppWrapper { MainScreen() } }
                    }
                    return@collect
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = NavigationItems.get()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController = navController, items = items)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            // 默认把 HomeScreen 设置为默认页面
            startDestination = items[0].route,
            modifier = Modifier.padding(innerPadding)
        ) {
            items.forEach { item ->
                composable(item.route) {
                    item.screen()
                }
            }
        }
    }
}



