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

// DataSource 键
private val ACCOUNT_KEY = stringPreferencesKey("user_account")
private val ENCRYPTED_PASSWORD_KEY = stringPreferencesKey("encrypted_password")

// 加密配置
private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
private val SECRET_KEY = "ThiIsAValid32BytesKeyForAES256!!".toByteArray() // 需替换为真实密钥
private val IV = "16BytesIVHere!!!".toByteArray() // 需替换为真实IV

// DataStore 键定义
private val TOKEN_KEY = stringPreferencesKey("auth_token")
private val USER_ID_KEY = longPreferencesKey("user_id")
private val REMEMBER_ME_KEY = booleanPreferencesKey("remember_me")
private val AUTO_LOGIN_KEY = booleanPreferencesKey("auto_login")

/**
 * 单例对象 LoginManager --- 数据层 --- 仅处理数据层逻辑
 * 专注于核心的业务逻辑 与界面分离
 * 单例对象 - 存活于整个生命周期
 * 解耦 业务逻辑 与 界面
 */
object LoginManager {
    // 初始化完成状态
    private val _initializationComplete = MutableStateFlow(false)
    val initializationComplete = _initializationComplete.asStateFlow()

    private val _account = mutableStateOf("")
    val account: State<String> get() = _account


    private lateinit var dataStore: DataStore<Preferences>

    // 新增 Flow 用于观察
    val isLoggedInFlow = MutableStateFlow(false)


    // 登录状态
    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> get() = _isLoggedIn

    // 用户信息
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> get() = _currentUser

    /**
     * 记录是否初始化
     */
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

    /**
     * 获取凭证方法
     */
    suspend fun getCredentials(): Pair<String, String>? {
        return dataStore.data.map { prefs ->
            val account = prefs[ACCOUNT_KEY]
            val encryptedPwd = prefs[ENCRYPTED_PASSWORD_KEY]
            if (account != null && encryptedPwd != null) {
                Pair(account, decrypt(encryptedPwd))
            } else {
                null
            }
        }.firstOrNull()
    }

    /**
     * 处理登录成功
     * @param token 认证令牌
     * @param user 用户数据
     * @param rememberMe 记住登录状态
     * @param autoLogin 下次自动登录
     */
    suspend fun onLoginSuccess(
        account: String,
        password: String,
        user: User,
        rememberMe: Boolean = false,
        autoLogin: Boolean = false
    ) {
        // 加密敏感数据
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

    /**
     * 处理登出
     */
    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs.remove(ENCRYPTED_PASSWORD_KEY)
            prefs[AUTO_LOGIN_KEY] = false
            if (!prefs[REMEMBER_ME_KEY]!!) {
                prefs.remove(ACCOUNT_KEY)
            }
        }
        _isLoggedIn.value = false
    }


    /**
     * 获取认证令牌
     */
    suspend fun getAuthToken(): String? {
        return dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]?.let { decrypt(it) }
        }.firstOrNull()
    }

    // 私有方法：加载持久化数据
    private suspend fun loadPersistedData() {
        try {
            val prefs = dataStore.data.map { it }.firstOrNull() ?: return

            _account.value = prefs[ACCOUNT_KEY] ?: ""

            when {
                prefs[AUTO_LOGIN_KEY] == true -> autoLogin()
                prefs[REMEMBER_ME_KEY] == true -> loadRememberedAccount()
            }
        } finally {
            _initializationComplete.value = true // 标记初始化完成
        }
    }


    private suspend fun autoLogin() {
        getCredentials()?.let { (account, pwd) ->
            try {
                // TODO: 使用常量 替换为实际验证逻辑
                val isValid = account == "admin" && pwd == "123456"
                _isLoggedIn.value = isValid
                if (isValid) {
                    // 保持与正常登录相同的处理流程
                    onLoginSuccess(
                        account = account,
                        password = pwd,
                        user = fetchUser(account), // 需要添加用户获取方法
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
        // 仅显示已记住的账号
        _account.value.takeIf { it.isNotEmpty() }?.let {
            // 通知UI层填充账号
        }
    }


    // 加密方法
    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val keySpec = SecretKeySpec(SECRET_KEY, "AES")
        val ivSpec = IvParameterSpec(IV)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return Base64.encodeToString(cipher.doFinal(value.toByteArray()), Base64.NO_WRAP)
    }

    // 解密方法
    private fun decrypt(encrypted: String): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val keySpec = SecretKeySpec(SECRET_KEY, "AES")
        val ivSpec = IvParameterSpec(IV)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        return String(cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)))
    }
}

// 扩展属性：加密 DataStore
//private val Context.encryptedDataStore: DataStore<Preferences> by preferencesDataStore(
//    name = "secure_login",
//    produceMigrations = { context ->
//        listOf(EncryptedDataStoreMigration(context))
//    }
//)

private fun fetchUser(account: String): User {
    return User(
        id = 1,
        username = "Javier Chen",
        email = "javierchen@gmail.com",
        avatar = "https://avatars.githubusercontent.com/u/10290408?v=4"
    )
}
