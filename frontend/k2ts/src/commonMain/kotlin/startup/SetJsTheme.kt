package startup

import settings.models.AppTheme
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setJsTheme(theme: AppTheme) {
    val html = document.documentElement ?: return
    when (theme) {
        AppTheme.Dark -> {
            html.setAttribute("data-theme", "dark")
            html.classList.add("dark")
        }
        AppTheme.Light -> {
            html.setAttribute("data-theme", "light")
            html.classList.remove("dark")
        }
        AppTheme.System -> {
            val isDark = window.matchMedia("(prefers-color-scheme: dark)").matches
            if (isDark) {
                html.setAttribute("data-theme", "dark")
                html.classList.add("dark")
            } else {
                html.setAttribute("data-theme", "light")
                html.classList.remove("dark")
            }
        }
    }
}
