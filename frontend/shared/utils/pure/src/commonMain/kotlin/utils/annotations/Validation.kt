package utils.annotations

import kotlin.reflect.KClass

interface ValidationRule<T> {
    fun validate(value: T, param: Any? = null): String?
}

@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class LinkedRule(
    @Suppress("unused")
    val ruleClass: KClass<out ValidationRule<*>>
)

@Target(AnnotationTarget.CLASS)
annotation class GenerateValidator