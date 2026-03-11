package utils.data

inline fun <R> throwableToException(block: () -> R) = runCatching {
    block()
}.fold(
    onSuccess = { data -> data },
    onFailure = { error -> throw (error as? Exception ?: RuntimeException(error)) }
)
