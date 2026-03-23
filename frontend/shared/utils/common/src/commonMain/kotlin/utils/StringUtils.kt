package utils

fun String.snakeCase(): String =
    buildString {
        for (c in this@snakeCase) {
            if (c.isUpperCase() && isNotEmpty()) {
                append('_')
            }

            append(c.lowercaseChar())
        }
    }

fun String?.orUnknown(fallback: String = "Unknown error!"): String = this ?: fallback
val String?.orUnknown: String
    get() = this.orUnknown()