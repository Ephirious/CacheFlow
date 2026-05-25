package settings.repositories

import settings.models.AppTheme

interface SettingsRepository {

    fun setTheme(theme: AppTheme)
    fun getTheme(): AppTheme
}