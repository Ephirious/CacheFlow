package utils


actual fun pushUrlSegment(segment: String) {
    val currentUrl = kotlinx.browser.window.location.pathname
    if (!currentUrl.contains(segment)) {
        kotlinx.browser.window.history.pushState(null, "", "$currentUrl/$segment")
    }
}

actual fun popUrlSegment(segment: String) {
    val currentUrl = kotlinx.browser.window.location.pathname
    if (currentUrl.contains(segment)) {
        val newUrl = currentUrl.replace("/$segment", "")
        kotlinx.browser.window.history.replaceState(null, "", newUrl)
    }
}