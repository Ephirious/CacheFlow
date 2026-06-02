package utils

import kotlinx.browser.window

actual fun getFromEnv(key: String): String? {
    val envObj = window.asDynamic().APP_ENV
    return if (envObj != null && envObj[key] != undefined) {
        envObj[key].toString()
    } else {
        null
    }
}