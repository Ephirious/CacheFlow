package utils

enum class LogLevel { TRACE, DEBUG, INFO, WARN, ERROR }

expect object Logg {
    var formatter: LogFormatter
    fun setup(tag: String?, level: LogLevel = LogLevel.TRACE)

    fun error(tag: String? = null, throwable: Throwable? = null, message: () -> Any?)
    fun warn(tag: String? = null, message: () -> Any?)
    fun info(tag: String? = null, message: () -> Any?)
    fun debug(tag: String? = null, message: () -> Any?)
}

data class LogEvent(
    val level: LogLevel,
    val platformTag: String?,
    val internalTag: String?,
    val message: String,
    val throwable: Throwable? = null
)

interface LogFormatter {
    fun format(event: LogEvent): String
}

class DefaultCleanFormatter : LogFormatter {
    override fun format(event: LogEvent): String {
        return buildString {
            if (event.platformTag != null) append("[${event.platformTag}]")
            if (event.internalTag != null) append("[${event.internalTag}]")
            append(" ${event.level.name}: ${event.message}")
            event.throwable?.let { append(" | Error: ${it.message}") }
        }
    }
}