package `fun`.javierchen.english_review.model.manager

data class LoginUIState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)