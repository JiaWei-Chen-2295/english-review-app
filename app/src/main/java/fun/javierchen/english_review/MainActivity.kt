package `fun`.javierchen.english_review

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `fun`.javierchen.english_review.components.AppBottomBar
import `fun`.javierchen.english_review.model.NavigationItems
import `fun`.javierchen.english_review.screens.DiceRollerApp
import `fun`.javierchen.english_review.screens.ImportScreen
import `fun`.javierchen.english_review.screens.ProfileScreen
import `fun`.javierchen.english_review.ui.theme.English_reviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            English_reviewTheme {
                MainScreen()
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
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { DiceRollerApp() }
            composable("import") { ImportScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}



