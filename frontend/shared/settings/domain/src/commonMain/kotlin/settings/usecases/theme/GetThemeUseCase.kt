package settings.usecases.theme

import settings.repositories.SettingsRepository

class GetThemeUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke() = repository.getTheme()
}