package settings.repositories

import settings.local.SettingsLocalDataSource
import settings.models.AppTheme

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource,
) : SettingsRepository {
    override fun setTheme(theme: AppTheme) {
        localDataSource.setTheme(theme)
    }

    override fun getTheme(): AppTheme = localDataSource.getTheme()
}