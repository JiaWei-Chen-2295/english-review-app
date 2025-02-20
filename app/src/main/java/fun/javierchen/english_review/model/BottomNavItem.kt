package `fun`.javierchen.english_review.model

import androidx.compose.runtime.Composable

/**
 * 定义底部导航栏数据
 */
data class BottomNavItem(
    val nameRes: Int,
    val route: String,
    val icon: Int,
    /**
     * 用于组件映射
     */
    val screen: @Composable () -> Unit

)