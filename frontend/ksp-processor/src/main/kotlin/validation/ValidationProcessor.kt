package validation

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class ValidationProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("utils.annotations.GenerateValidator")
        symbols.filterIsInstance<KSClassDeclaration>().forEach { it.accept(ValidationVisitor(), Unit) }
        return emptyList()
    }

    inner class ValidationVisitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()

            val isDataCopyable = classDeclaration.annotations.any {
                it.shortName.asString() == "DataCopyable" || it.shortName.asString() == "DataCopyableNode"
            }

            val hasGenerics = classDeclaration.typeParameters.isNotEmpty()
            val validatableProperties = classDeclaration.getAllProperties()
                .filter { (it.isAbstract() || it.hasBackingField) && hasLinkedRule(it) }.toList()

            if (validatableProperties.isEmpty()) return

            val enumName = "${className}ValidationFields"
            val errorsName = "${className}ValidationErrors"
            val receiverType = if (hasGenerics) "$className<$errorsName>" else className

            val ruleImports = validatableProperties.flatMap { prop ->
                prop.annotations.mapNotNull { ann ->
                    val linkedRule = ann.annotationType.resolve().declaration.annotations
                        .find { it.shortName.asString() == "LinkedRule" }
                    (linkedRule?.arguments?.firstOrNull()?.value as? KSType)?.declaration?.qualifiedName?.asString()
                }
            }.distinct()

            codeGenerator.createNewFile(
                Dependencies(false, classDeclaration.containingFile!!),
                packageName,
                "${className}Validators"
            ).writer().use { w ->
                w.write("package $packageName\n\nimport kotlin.js.JsExport\n")
                ruleImports.forEach { w.write("import $it\n") }
                w.write("\n")

                w.write("@JsExport\n")
                w.write("enum class $enumName { ${validatableProperties.joinToString(", ") { it.simpleName.asString() }} }\n\n")

                w.write("@JsExport\n")
                w.write("data class $errorsName(\n")
                validatableProperties.forEach { w.write("    val ${it.simpleName.asString()}: String? = null,\n") }
                w.write(") {\n    val hasErrors get() = ${validatableProperties.joinToString(" || ") { "${it.simpleName.asString()} != null" }}\n}\n\n")

                w.write("fun $receiverType.validate(field: $enumName): String? = when(field) {\n")
                validatableProperties.forEach { prop ->
                    w.write("    $enumName.${prop.simpleName.asString()} -> {\n")
                    generateValidationLogic(prop, w)
                    w.write("    }\n")
                }
                w.write("}\n\n")

                w.write("fun $receiverType.validate(): $errorsName = $errorsName(\n")
                validatableProperties.forEach {
                    val name = it.simpleName.asString()
                    w.write("    $name = validate($enumName.$name),\n")
                }
                w.write(")\n\n")

                val hasValidationField = classDeclaration.getAllProperties().any { it.simpleName.asString() == "validation" }

                if (isDataCopyable) {
                    w.write("fun <T : $receiverType> T.validated(field: $enumName): T {\n")
                    generateValidatedBody(w, enumName, validatableProperties, "this.copyBase(validation = newValidation) as T")
                    w.write("}\n")
                } else if (hasValidationField) {
                    w.write("fun $receiverType.validated(field: $enumName): $receiverType {\n")
                    generateValidatedBody(w, enumName, validatableProperties, "this.copy(validation = newValidation)")
                    w.write("}\n")
                }
            }
        }

        private fun generateValidatedBody(w: java.io.Writer, enumName: String, props: List<KSPropertyDeclaration>, returnStatement: String) {
            w.write("    val error = validate(field)\n")
            w.write("    val newValidation = when(field) {\n")
            props.forEach { prop ->
                val pName = prop.simpleName.asString()
                w.write("        $enumName.$pName -> validation.copy($pName = error)\n")
            }
            w.write("    }\n")
            w.write("    return $returnStatement\n")
        }

        private fun hasLinkedRule(prop: KSPropertyDeclaration): Boolean =
            prop.annotations.any { it.annotationType.resolve().declaration.annotations.any { a -> a.shortName.asString() == "LinkedRule" } }

        private fun generateValidationLogic(prop: KSPropertyDeclaration, w: java.io.Writer) {
            val propName = "this.${prop.simpleName.asString()}"
            val rules = prop.annotations.filter {
                it.annotationType.resolve().declaration.annotations.any { a -> a.shortName.asString() == "LinkedRule" }
            }.toList()

            w.write("        ")
            rules.forEachIndexed { i, ann ->
                val linked = ann.annotationType.resolve().declaration.annotations.first { it.shortName.asString() == "LinkedRule" }
                val ruleName = (linked.arguments.first().value as KSType).declaration.simpleName.asString()
                val paramArg = ann.arguments.find { it.name?.asString() == "param" || it.name?.asString() == "value" }?.value
                val param = when (paramArg) {
                    is String -> "\"$paramArg\""
                    null -> "null"
                    else -> paramArg.toString()
                }
                val msg = ann.arguments.find { it.name?.asString() == "message" }?.value as? String ?: ""
                val call = "$ruleName.validate($propName, $param)"
                w.write(if (msg.isNotEmpty()) "$call?.let { \"$msg\" }" else call)
                if (i < rules.size - 1) w.write("\n            ?: ")
            }
            w.write("\n")
        }
    }
}