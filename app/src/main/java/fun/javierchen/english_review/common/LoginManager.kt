// LoginManager.kt
package `fun`.javierchen.english_review.common

import android.content.Context
import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import `fun`.javierchen.english_review.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object LoginManager {
    // region 常量声明
    private val ACCOUNT_KEY = stringPreferencesKey("user_account")
    private val ENCRYPTED_PASSWORD_KEY = stringPreferencesKey("encrypted_password")
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = longPreferencesKey("user_id")
    private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
    private val AUTO_LOGIN_KEY = booleanPreferencesKey("auto_login")

    private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
    private val SECRET_KEY = "ThiIsAValid32BytesKeyForAES256!!".toByteArray()
    private val IV = "16BytesIVHere!!!".toByteArray()
    // endregion

    // region 状态管理
    private val _initializationComplete = MutableStateFlow(false)
    val initializationComplete = _initializationComplete.asStateFlow()

    private val _account = mutableStateOf("")
    val account: State<String> get() = _account

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> get() = _isLoggedIn

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> get() = _currentUser

    val isLoggedInFlow = MutableStateFlow(false)
    // endregion

    // region 数据存储
    private lateinit var dataStore: DataStore<Preferences>
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    dataStore = PreferenceDataStoreFactory.create(
                        produceFile = { context.preferencesDataStoreFile("secure_prefs") }
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        loadPersistedData()
                    }
                    isInitialized = true
                }
            }
        }
    }
    // endregion

    // region 公开方法
    suspend fun getCredentials(): Pair<String, String>? {
        return dataStore.data.map { prefs ->
            prefs[ACCOUNT_KEY]?.let { account ->
                prefs[ENCRYPTED_PASSWORD_KEY]?.let { encryptedPwd ->
                    Pair(account, decrypt(encryptedPwd))
                }
            }
        }.firstOrNull()
    }

    suspend fun onLoginSuccess(
        account: String,
        password: String,
        user: User,
        rememberMe: Boolean = false,
        autoLogin: Boolean = false
    ) {
        val encryptedPwd = encrypt(password)
        dataStore.edit { prefs ->
            prefs[ACCOUNT_KEY] = account
            prefs[ENCRYPTED_PASSWORD_KEY] = encryptedPwd
            prefs[REMEMBER_ME_KEY] = rememberMe
            prefs[AUTO_LOGIN_KEY] = autoLogin
            prefs[USER_ID_KEY] = user.id
        }

        _account.value = account
        _currentUser.value = user
        _isLoggedIn.value = true
        isLoggedInFlow.value = true
    }

    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs.remove(ENCRYPTED_PASSWORD_KEY)
            prefs[AUTO_LOGIN_KEY] = false
            if (!(prefs[REMEMBER_ME_KEY] ?: false)) {
                prefs.remove(ACCOUNT_KEY)
            }
        }
        _isLoggedIn.value = false
        isLoggedInFlow.value = false
    }

    suspend fun getAuthToken(): String? {
        return dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]?.let { decrypt(it) }
        }.firstOrNull()
    }
    // endregion

    // region 私有方法
    private suspend fun loadPersistedData() {
        try {
            dataStore.data.first().let { prefs ->
                _account.value = prefs[ACCOUNT_KEY] ?: ""
                when {
                    prefs[AUTO_LOGIN_KEY] == true -> autoLogin()
                    prefs[REMEMBER_ME_KEY] == true -> loadRememberedAccount()
                }
            }
        } finally {
            _initializationComplete.value = true
        }
    }

    private suspend fun autoLogin() {
        getCredentials()?.let { (account, pwd) ->
            try {
                val isValid = account == "admin" && pwd == "123456"
                _isLoggedIn.value = isValid
                if (isValid) {
                    onLoginSuccess(
                        account = account,
                        password = pwd,
                        user = fetchUser(account),
                        rememberMe = dataStore.data.first()[REMEMBER_ME_KEY] ?: false,
                        autoLogin = dataStore.data.first()[AUTO_LOGIN_KEY] ?: true
                    )
                } else {
                    logout()
                }
            } catch (e: Exception) {
                _isLoggedIn.value = false
            }
        }
    }

    private fun loadRememberedAccount() {
        _account.value.takeIf { it.isNotEmpty() }?.let {
            // 通知UI层填充账号
        }
    }
    // endregion

    // region 加密工具
    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE,
                SecretKeySpec(SECRET_KEY, "AES"),
                IvParameterSpec(IV))
        }
        return Base64.encodeToString(cipher.doFinal(value.toByteArray()), Base64.NO_WRAP)
    }

    private fun decrypt(encrypted: String): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE,
                SecretKeySpec(SECRET_KEY, "AES"),
                IvParameterSpec(IV))
        }
        return String(cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)))
    }
    // endregion

    // region 辅助方法
    private fun fetchUser(account: String): User {
        return User(
            id = 1,
            username = "Javier Chen",
            email = "javierchen@gmail.com",
            avatar = "https://avatars.githubusercontent.com/u/10290408?v=4"
        )
    }
    // endregion
}
