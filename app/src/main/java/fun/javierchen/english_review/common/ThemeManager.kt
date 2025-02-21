package `fun`.javierchen.english_review.common

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// 定义全局常量
private val THEME_MODE = intPreferencesKey("dark_theme")

/**
 * 全局主题管理器
 */
object ThemeManager {
    private val _themeMode = mutableStateOf(2)
    /**
     *  0=light, 1=dark, 2=system
     */
    val themeMode: State<Int> get() = _themeMode


    // 需要传入 context 参数
    fun toggleTheme(context: Context, mode: Int) {
        _themeMode.value = mode
        CoroutineScope(Dispatchers.IO).launch {
            saveThemePreference(context, mode)
        }
    }

    private suspend fun saveThemePreference(context: Context, mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    // 添加初始化方法
    suspend fun initialize(context: Context) {
        val saved = context.dataStore.data.map { preferences:Preferences ->
            preferences[THEME_MODE] ?: 2
        }.first()
        _themeMode.value = saved
    }
}

// DataStore 扩展属性需要放在类外
private val Context.dataStore by preferencesDataStore(name = "settings")
