package `fun`.javierchen.english_review.components.wrapper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import `fun`.javierchen.english_review.common.ThemeManager
import `fun`.javierchen.english_review.ui.theme.AppTheme

/**
 * 让组件在主题切换时保持一致
 */
@Composable
fun AppWrapper(content: @Composable () -> Unit) {
    val systemDarkTheme = isSystemInDarkTheme()
    val themeMode by ThemeManager.themeMode

    val actualDarkTheme = when (themeMode) {
        0 -> false
        1 -> true
        else -> systemDarkTheme
    }

    AppTheme(darkTheme = actualDarkTheme) {
        content()
    }
}


