package `fun`.javierchen.english_review.model

import ImportScreen
import `fun`.javierchen.english_review.R
import `fun`.javierchen.english_review.screens.DiceRollerApp

import `fun`.javierchen.english_review.screens.ProfileScreen

/**
 * 底部导航栏组件
 */
object NavigationItems {
    fun get() = listOf(
        BottomNavItem(
            nameRes = R.string.home,
            route = "home",
            icon = R.drawable._home_2,
            screen = { DiceRollerApp() }
        ),
        BottomNavItem(
            nameRes = R.string._import,
            route = "import",
            icon = R.drawable._import,
            screen = { ImportScreen() }
        ),
        BottomNavItem(
            nameRes = R.string.profile,
            route = "profile",
            icon = R.drawable._user,
            screen = { ProfileScreen() }
        )
    )
}
