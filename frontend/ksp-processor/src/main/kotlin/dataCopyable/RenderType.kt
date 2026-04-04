package dataCopyable

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance


fun KSType.renderType(): String {
    val declaration = this.declaration
    val baseName = declaration.qualifiedName?.asString() ?: declaration.simpleName.asString()

    val arguments = this.arguments
    val typeArgs = if (arguments.isNotEmpty()) {
        arguments.joinToString(prefix = "<", postfix = ">") { arg ->
            val variance = when (arg.variance) {
                Variance.INVARIANT -> ""
                Variance.CONTRAVARIANT -> "in "
                Variance.COVARIANT -> "out "
                else -> ""
            }
            val resolvedArg = arg.type?.resolve()
            "$variance${resolvedArg?.renderType() ?: "Any?"}"
        }
    } else {
        ""
    }

    val nullability = if (this.nullability == Nullability.NULLABLE) "?" else ""

    return "$baseName$typeArgs$nullability"
}