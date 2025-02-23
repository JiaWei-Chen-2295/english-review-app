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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化主题管理器
        lifecycleScope.launch(Dispatchers.IO) {
            ThemeManager.initialize(applicationContext)
        }


        // 初始化登录管理器
        LoginManager.initialize(applicationContext)

        // 如果没有登录 就跳转回登录页面
        if (!LoginManager.isLoggedIn.value) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        setContent {
            AppWrapper { MainScreen() }
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



