import org.koin.dsl.module
import settings.mvi.SettingsContainer

val settingsPresentationModule = module {
    factory<SettingsContainer> {
        SettingsContainer(get(), get())
    }
}