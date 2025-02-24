package `fun`.javierchen.english_review.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `fun`.javierchen.english_review.common.LoginManager
import `fun`.javierchen.english_review.model.User
import `fun`.javierchen.english_review.model.manager.LoginUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * ViewModel == 表现层 --- 将数据层的结果转换为 UI 需要的数据
 * MVVM 架构 单方向数据流
 * 与 MVC 的Controller不同点:
 *      - ViewModel 是数据转换器, Controller 是协调器 直接操控 Model 和 View
 */
class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()


    fun login(account: String, password: String, rememberMe: Boolean, autoLogin: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                if (account == "admin" && password == "123456") {
                    delay(1000)
                    val user = fetchUser(account)

                    // 直接调用挂起函数
                    LoginManager.onLoginSuccess(
                        account = account,
                        password = password,
                        user = user,
                        rememberMe = rememberMe,
                        autoLogin = autoLogin
                    )

                    _uiState.update {
                        it.copy(isLoginSuccess = true, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "登录失败: ${e.message}", isLoading = false)
                }
            }

        }
    }


}

private fun fetchUser(account: String): User {
    return User(
        id = 1,
        username = "Javier Chen",
        email = "javierchen@gmail.com",
        avatar = "https://avatars.githubusercontent.com/u/10290408?v=4"
    )
}

