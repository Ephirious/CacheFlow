package dataCopyable

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.symbol.Modifier

class DataCopyableProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val interfaces = resolver.getSymbolsWithAnnotation("utils.annotations.DataCopyable")
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE }

        val allNodes = resolver.getSymbolsWithAnnotation("utils.annotations.DataCopyableNode")
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.CLASS }
            .toList()

        interfaces.forEach { interfaceDec ->
            val packageName = interfaceDec.packageName.asString()
            val interfaceSimpleName = interfaceDec.simpleName.asString()
            val fullInterfaceName = interfaceDec.qualifiedName?.asString()
                ?.removePrefix("$packageName.") ?: interfaceSimpleName

            val typeParams = interfaceDec.typeParameters
            val hasGenerics = typeParams.isNotEmpty()
            val typeParamsNames = typeParams.joinToString(", ") { it.name.asString() }

            val fileName = fullInterfaceName.replace(".", "_") + "GeneratedCopy"
            val properties = interfaceDec.getAllProperties().toList()
            val imports = mutableSetOf<String>()

            fun resolveTypeName(reference: KSTypeReference?): String {
                val type = reference?.resolve() ?: return "Any"
                val decl = type.declaration

                if (decl is KSClassDeclaration) {
                    decl.qualifiedName?.asString()?.let { qName ->
                        if (!qName.startsWith("kotlin.") && qName.contains(".")) {
                            imports.add(qName)
                        }
                    }
                }

                val baseName = decl.simpleName.asString()
                val arguments = type.arguments

                val generics = if (arguments.isNotEmpty()) {
                    arguments.joinToString(prefix = "<", postfix = ">") { arg ->
                        val variance = when (arg.variance) {
                            Variance.CONTRAVARIANT -> "in "
                            Variance.COVARIANT -> "out "
                            else -> ""
                        }
                        variance + resolveTypeName(arg.type)
                    }
                } else ""

                return baseName + generics + (if (type.isMarkedNullable) "?" else "")
            }

            val propertiesWithTypes = properties.map { p ->
                val pName = p.simpleName.asString()
                val typeName = resolveTypeName(p.type)
                pName to typeName
            }

            val impls = allNodes.filter { node ->
                node.superTypes.any { it.resolve().declaration == interfaceDec }
            }
            if (impls.isEmpty()) return@forEach

            val sources = (impls.mapNotNull { it.containingFile } + interfaceDec.containingFile)
                .filterNotNull().distinct().toTypedArray()

            val file = codeGenerator.createNewFile(
                Dependencies(true, *sources),
                packageName,
                fileName
            )

            file.writer().use { writer ->
                writer.write("package $packageName\n\n")
                imports.sorted().distinct().forEach { writer.write("import $it\n") }
                writer.write("\n")

                val genericSignature = if (hasGenerics)
                    "<$typeParamsNames, T : $fullInterfaceName<$typeParamsNames>>"
                else "<T : $fullInterfaceName>"

                writer.write("@Suppress(\"UNCHECKED_CAST\", \"RedundantCast\")\n")
                writer.write("fun $genericSignature T.copyBase(\n")

                propertiesWithTypes.forEachIndexed { index, (pName, typeName) ->
                    val comma = if (index < propertiesWithTypes.size - 1) "," else ""
                    writer.write("    $pName: $typeName = this.$pName$comma\n")
                }
                writer.write("): T = when(this) {\n")

                impls.forEach { impl ->
                    val qName = impl.qualifiedName?.asString() ?: return@forEach
                    if (!impl.modifiers.contains(Modifier.DATA)) {
                        writer.write("    is $qName -> this as T\n")
                        return@forEach
                    }

                    val copyParams = properties.joinToString(", ") { p ->
                        val pName = p.simpleName.asString()
                        if (pName == "validation" && hasGenerics) {
                            "$pName = $pName as ${interfaceSimpleName}ValidationErrors"
                        } else {
                            "$pName = $pName"
                        }
                    }

                    val constructorParams = impl.primaryConstructor?.parameters?.map { it.name?.asString() }.orEmpty()
                    val delegateProp = impl.getAllProperties().find { prop ->
                        val propTypeDec = prop.type.resolve().declaration
                        prop.simpleName.asString() in constructorParams &&
                                propTypeDec is KSClassDeclaration &&
                                propTypeDec.superTypes.any { it.resolve().declaration == interfaceDec }
                    }

                    if (delegateProp != null) {
                        val dName = delegateProp.simpleName.asString()
                        writer.write("    is $qName -> this.copy($dName = this.$dName.copyBase($copyParams)) as T\n")
                    } else {
                        writer.write("    is $qName -> this.copy($copyParams) as T\n")
                    }
                }
                writer.write("    else -> this\n}\n")
            }
        }
        return emptyList()
    }
}