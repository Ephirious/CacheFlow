package settings.mvi

import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.api.Store
import pro.respawn.flowmvi.dsl.store
import settings.models.AppTheme.*
import settings.usecases.theme.GetThemeUseCase
import settings.usecases.theme.SetThemeUseCase
import utils.presentation.flowMVI.customReduce
import utils.presentation.flowMVI.fastConfig

class SettingsContainer(
    getThemeUseCase: GetThemeUseCase,
    setThemeUseCase: SetThemeUseCase,
) : Container<SettingsState, SettingsIntent, SettingsAction> {
    override val store: Store<SettingsState, SettingsIntent, SettingsAction> =
        store(
            initial = SettingsState(
                currentTheme = getThemeUseCase(),
            )
        ) {
            fastConfig(
                name = "Settings",
                resetOnStop = false,
                doOnRecover = null
            )


            customReduce { intent ->
                when (intent) {
                    SettingsIntent.ChangeTheme -> {
                        withState {
                            val nextTheme = when (this.currentTheme) {
                                System -> Dark
                                Dark -> Light
                                Light -> System
                            }
                            setThemeUseCase(nextTheme)
                            updateState {
                                copy(
                                    currentTheme = nextTheme,
                                )
                            }
                            action(SettingsAction.ThemeChanged(nextTheme))
                        }
                    }
                }
            }
        }
}