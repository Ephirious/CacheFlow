package utils.annotations

import kotlin.js.JsExport
import kotlin.reflect.KClass

interface ValidationRule<T, S, E> {
    fun validate(value: T, state: S, param: Any? = null): E?
}

@JsExport
interface ValidationError

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class LinkedRule(
    @Suppress("unused")
    val ruleClass: KClass<out ValidationRule<*, *, *>>
)

@Target(AnnotationTarget.CLASS)
annotation class GenerateValidator(
    @Suppress("unused")
    val errorClass: KClass<out ValidationError> = ValidationError::class
)