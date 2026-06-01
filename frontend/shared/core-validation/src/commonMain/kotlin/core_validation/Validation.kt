package core_validation

import utils.CustomError
import kotlin.js.JsExport
import kotlin.reflect.KClass

interface ValidationRule<T, S, P, E> {
    fun validate(value: T, ctx: S, param: P): E?
}


@JsExport
interface ValidationError : CustomError

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class LinkedRule(
    @Suppress("unused")
    val ruleClass: KClass<out ValidationRule<*, *, *, *>>
)

@Target(AnnotationTarget.CLASS)
annotation class GenerateValidator(
    @Suppress("unused")
    val errorClass: KClass<out ValidationError> = ValidationError::class
)


fun <T, S> combineRules(
    value: T,
    ctx: S,
    vararg rules: () -> CustomError?
): CustomError? {
    for (rule in rules) {
        val error = rule()
        if (error != null) return error
    }
    return null
}