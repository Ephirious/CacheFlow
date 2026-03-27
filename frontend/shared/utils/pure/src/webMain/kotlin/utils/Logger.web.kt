package utils

actual object Logg {

    actual var formatter: LogFormatter = DefaultCleanFormatter()

    private var platformTag: String? = null
    private var currentLevel: LogLevel = LogLevel.TRACE

    actual fun setup(tag: String?, level: LogLevel) {
        platformTag = tag
        currentLevel = level
    }

    private fun log(level: LogLevel, internalTag: String?, throwable: Throwable?, message: () -> Any?) {
        if (level.ordinal < currentLevel.ordinal) return

        val event = LogEvent(level, platformTag, internalTag, message().toString(), throwable)
        val formattedMsg = formatter.format(event)

        when (level) {
            LogLevel.ERROR, LogLevel.TRACE -> console.error(formattedMsg, throwable ?: "")
            LogLevel.WARN -> console.warn(formattedMsg)
            LogLevel.INFO -> console.info(formattedMsg)
            LogLevel.DEBUG -> console.asDynamic().debug(formattedMsg)
        }
    }

    actual fun info(tag: String?, message: () -> Any?) = log(LogLevel.INFO, tag, null, message)
    actual fun warn(tag: String?, message: () -> Any?) =
        log(LogLevel.WARN, tag, null, message)

    actual fun debug(tag: String?, message: () -> Any?) =
        log(LogLevel.DEBUG, tag, null, message)

    actual fun error(tag: String?, throwable: Throwable?, message: () -> Any?) =
        log(LogLevel.ERROR, tag, throwable, message)
}