package settings.mvi

import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState
import settings.models.AppTheme
import kotlin.js.JsExport

@JsExport
data class SettingsState(
    val currentTheme: AppTheme
) : MVIState

@JsExport
sealed class SettingsIntent : MVIIntent {
    data object ChangeTheme : SettingsIntent()
}

@JsExport
sealed class SettingsAction : MVIAction {
    data class ThemeChanged(val theme: AppTheme) : SettingsAction()
}