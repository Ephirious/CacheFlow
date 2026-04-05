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
        val unableToProcess = symbols.filterNot { it is KSClassDeclaration }
        symbols.filterIsInstance<KSClassDeclaration>().forEach { it.accept(ValidationVisitor(), Unit) }
        return unableToProcess.toList()
    }

    inner class ValidationVisitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()

            val validatableProperties = classDeclaration.getAllProperties()
                .filter { prop ->
                    (prop.isAbstract() || prop.hasBackingField) &&
                            prop.annotations.any { ann ->
                                ann.annotationType.resolve().declaration.annotations.any {
                                    it.shortName.asString() == "LinkedRule"
                                }
                            }
                }.toList()

            if (validatableProperties.isEmpty()) return

            val enumName = "${className}ValidationFields"
            val errorsName = "${className}ValidationErrors"

            val ruleImports = validatableProperties.flatMap { prop ->
                prop.annotations.mapNotNull { ann ->
                    val linkedRule = ann.annotationType.resolve().declaration.annotations
                        .find { it.shortName.asString() == "LinkedRule" }
                    val ruleType = linkedRule?.arguments?.firstOrNull()?.value as? KSType
                    ruleType?.declaration?.qualifiedName?.asString()
                }
            }.distinct()

            val file = codeGenerator.createNewFile(
                Dependencies(false, classDeclaration.containingFile!!),
                packageName,
                "${className}Validators"
            )

            file.writer().use { w ->
                w.write("package $packageName\n\n")
                w.write("import kotlin.js.JsExport\n")
                ruleImports.forEach { w.write("import $it\n") }
                w.write("\n")

                w.write("@JsExport\n")
                w.write(
                    "enum class $enumName { " +
                            validatableProperties.joinToString(", ") { it.simpleName.asString() } + " }\n\n")

                w.write("@JsExport\n")
                w.write("data class $errorsName(\n")
                validatableProperties.forEach { w.write("    val ${it.simpleName.asString()}: String? = null,\n") }
                w.write(") {\n")
                w.write(
                    "    val hasErrors get() = " +
                            validatableProperties.joinToString(" || ") { "${it.simpleName.asString()} != null" } + "\n")
                w.write("}\n\n")

                w.write("fun $className.validateField(field: $enumName): String? = when(field) {\n")
                validatableProperties.forEach { prop ->
                    w.write("    $enumName.${prop.simpleName.asString()} -> {\n")
                    generateValidationLogic(prop, w)
                    w.write("    }\n")
                }
                w.write("}\n\n")

                w.write("fun $className.validate(): $errorsName = $errorsName(\n")
                validatableProperties.forEach {
                    val name = it.simpleName.asString()
                    w.write("    $name = validateField($enumName.$name),\n")
                }
                w.write(")\n")


                // ЗАХАРДКОЖЕНО ДЛЯ @MaxLen
                w.write("@JsExport\n")
                w.write("object ${className}ValidationMetadata {\n")
                validatableProperties.forEach { prop ->
                    val propName = prop.simpleName.asString()
                    val maxLenAnn = prop.annotations.find { it.shortName.asString() == "MaxLen" }
                    val limit = maxLenAnn?.arguments?.find { it.name?.asString() == "param" }?.value

                    if (limit != null) {
                        w.write("    const val ${propName}MaxLen: Int = $limit\n")
                    }
                }
                w.write("}\n")
            }
        }

        private fun generateValidationLogic(prop: KSPropertyDeclaration, w: java.io.Writer) {
            val propName = "this.${prop.simpleName.asString()}"
            val annotations = prop.annotations.filter { ann ->
                ann.annotationType.resolve().declaration.annotations.any { it.shortName.asString() == "LinkedRule" }
            }.toList()

            annotations.forEachIndexed { index, ann ->
                val linkedRule = ann.annotationType.resolve().declaration.annotations
                    .first { it.shortName.asString() == "LinkedRule" }

                val ruleType = linkedRule.arguments.first().value as KSType
                val ruleClassName = ruleType.declaration.simpleName.asString()

                val param = ann.arguments.find { it.name?.asString() == "param" }?.value ?: "null"
                val customMsg = ann.arguments.find { it.name?.asString() == "message" }?.value as? String ?: ""

                val prefix = if (index == 0) "if" else "else if"
                w.write("        $prefix (!$ruleClassName.validate($propName, $param)) ")
                w.write("if (\"$customMsg\".isNotEmpty()) \"$customMsg\" else $ruleClassName.errorMessage($param)\n")
            }
            w.write("        else null\n")
        }
    }
}