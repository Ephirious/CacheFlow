package settings.pages

import kotlin.js.JsExport


@JsExport
interface SettingsPageComponent {
    val onCreateClick: () -> Unit
    val onItemClick: (id: String) -> Unit
}