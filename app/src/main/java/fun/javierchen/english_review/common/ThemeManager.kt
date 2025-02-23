package `fun`.javierchen.english_review.common

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import `fun`.javierchen.english_review.model.ThemeSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// 全局常量
private val THEME_MODE = intPreferencesKey("dark_theme")
private val IS_DYNAMIC_THEME = booleanPreferencesKey("is_dynamic_theme")


/**
 * 全局主题管理器
 */
object ThemeManager {
    private val _themeMode: MutableState<Int> = mutableStateOf(2)
    private val _dynamicTheme = mutableStateOf(false)
    /**
     *  0=light, 1=dark, 2=system
     */
    val themeMode: State<Int> get() = _themeMode
    val isDynamicTheme: State<Boolean> get() = _dynamicTheme

    /**
     * 修改黑白主题
     * @param context
     * @param mode 0=light, 1=dark, 2=system
     */
    fun toggleTheme(context: Context, mode: Int) {
        _themeMode.value = mode
        CoroutineScope(Dispatchers.IO).launch {
            saveThemePreference(context, mode)
        }
    }

    /**
     * 修改是否启用动态主题
     * @param context
     * @param isDynamicTheme true=启用，false=禁用
     */
    fun toggleDynamicTheme(context: Context, isDynamicTheme: Boolean) {
        _dynamicTheme.value = isDynamicTheme
        CoroutineScope(Dispatchers.IO).launch {
            saveDynamicThemePreference(context, isDynamicTheme)
        }
    }

    private suspend fun saveThemePreference(context: Context, mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    private suspend fun saveDynamicThemePreference(context: Context, isDynamicTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_THEME] = isDynamicTheme
        }
    }


    // 添加初始化方法
    suspend fun initialize(context: Context) {
        val saved = context.dataStore.data.map { preferences:Preferences ->
            ThemeSettings(
                themeMode = preferences[THEME_MODE] ?: 2,
                isDynamicTheme = preferences[IS_DYNAMIC_THEME] ?: false
            )
        }.first()
        _themeMode.value = saved.themeMode
        _dynamicTheme.value = saved.isDynamicTheme
    }
}

// DataStore 扩展属性需要放在类外
private val Context.dataStore by preferencesDataStore(name = "settings")
