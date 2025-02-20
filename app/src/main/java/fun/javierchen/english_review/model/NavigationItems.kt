package `fun`.javierchen.english_review.model

import `fun`.javierchen.english_review.R

object NavigationItems {
    fun get() = listOf(
        BottomNavItem(
            name = "Home",
            route = "home",
            icon = R.drawable._home_2
        ),
        BottomNavItem(
            name = "Import",
            route = "import",
            icon = R.drawable._import
        ),
        BottomNavItem(
            name = "Profile",
            route = "profile",
            icon = R.drawable._user
        ),
    )
}