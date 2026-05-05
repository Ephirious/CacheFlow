package settings.usecases.theme

import settings.models.AppTheme
import settings.repositories.SettingsRepository

class SetThemeUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(theme: AppTheme) = repository.setTheme(theme)
}