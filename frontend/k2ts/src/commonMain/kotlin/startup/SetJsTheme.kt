package startup

import settings.models.AppTheme

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setJsTheme(theme: AppTheme) {
    // TODO: темирование из theme.name
}