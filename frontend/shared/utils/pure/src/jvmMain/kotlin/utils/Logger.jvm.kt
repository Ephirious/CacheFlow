package utils

actual object Logg {
    actual var formatter: LogFormatter
        get() = TODO("Not yet implemented")
        set(value) {}

    actual fun setup(tag: String?, level: LogLevel) {
    }

    actual fun error(tag: String?, throwable: Throwable?, message: () -> Any?) {
    }

    actual fun warn(tag: String?, message: () -> Any?) {
    }

    actual fun info(tag: String?, message: () -> Any?) {
    }

    actual fun debug(tag: String?, message: () -> Any?) {
    }
}