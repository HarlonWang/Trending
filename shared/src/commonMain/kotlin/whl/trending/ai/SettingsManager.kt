package whl.trending.ai

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getIntFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode(val title: String) {
    FOLLOW_SYSTEM("跟随系统"),
    LIGHT("浅色"),
    DARK("深色")
}

class SettingsManager(private val settings: ObservableSettings) {
    private val THEME_KEY = "prefs_theme_mode"

    val themeMode: Flow<ThemeMode> = settings.getIntFlow(THEME_KEY, ThemeMode.FOLLOW_SYSTEM.ordinal)
        .map { ThemeMode.entries.getOrElse(it) { ThemeMode.FOLLOW_SYSTEM } }

    fun setThemeMode(mode: ThemeMode) {
        settings.putInt(THEME_KEY, mode.ordinal)
    }
}

val globalSettings by lazy { Settings() as ObservableSettings }
val globalSettingsManager by lazy { SettingsManager(globalSettings) }
