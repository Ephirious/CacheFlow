import org.koin.dsl.module
import settings.local.SettingsLocalDataSource
import settings.repositories.SettingsRepository
import settings.repositories.SettingsRepositoryImpl
import settings.usecases.theme.GetThemeUseCase
import settings.usecases.theme.SetThemeUseCase

val settingsDataModule = module {
    single<SettingsLocalDataSource> { SettingsLocalDataSource(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }


    factory<SetThemeUseCase> { SetThemeUseCase(get()) }
    factory<GetThemeUseCase> { GetThemeUseCase(get()) }
}