package settings.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import settings.models.AppTheme

class SettingsLocalDataSource(
    val settings: Settings
) {
    fun setTheme(theme: AppTheme) {
        settings[APP_THEME_KEY] = theme.name
    }

    fun getTheme(): AppTheme =
        AppTheme.valueOf(settings[APP_THEME_KEY, AppTheme.System.name])


    companion object {
        const val APP_THEME_KEY = "appThemeKey"
    }
}