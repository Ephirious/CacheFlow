package dataCopyable

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

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

        logger.warn("KSP: Found ${interfaces.count()} interfaces, ${allNodes.count()} nodes")

        interfaces.forEach { interfaceDec ->
            val packageName = interfaceDec.packageName.asString()
            val interfaceName = interfaceDec.simpleName.asString()
            val properties = interfaceDec.getAllProperties().toList()

            val impls = allNodes.filter { node ->
                node.superTypes.any { it.resolve().declaration == interfaceDec }
            }

            if (impls.isEmpty()) return@forEach

            val args = properties.joinToString(",\n    ") { p ->
                val typeName = p.type.resolve().declaration.qualifiedName?.asString() ?: "Any"
                "${p.simpleName.asString()}: $typeName = this.${p.simpleName.asString()}"
            }

            val sources = (impls.mapNotNull { it.containingFile } + interfaceDec.containingFile)
                .filterNotNull().distinct().toTypedArray()

            val file = codeGenerator.createNewFile(
                Dependencies(true, *sources),
                packageName,
                "${interfaceName}GeneratedCopy"
            )

            file.writer().use { writer ->
                writer.write("package $packageName\n\n")
                writer.write("@Suppress(\"UNCHECKED_CAST\", \"RedundantCast\")\n")
                writer.write("fun <T : $interfaceName> T.copyBase(\n    $args\n): T = when(this) {\n")

                impls.forEach { impl ->
                    val qName = impl.qualifiedName?.asString() ?: return@forEach
                    if (!impl.modifiers.contains(Modifier.DATA)) {
                        writer.write("    is $qName -> this as T\n")
                        return@forEach
                    }

                    val constructorParams = impl.primaryConstructor?.parameters?.map { it.name?.asString() }.orEmpty()
                    val delegateProp = impl.getAllProperties().find { prop ->
                        val isInterface = prop.type.resolve().declaration.let { d ->
                            d is KSClassDeclaration && d.superTypes.any { it.resolve().declaration == interfaceDec }
                        }
                        prop.simpleName.asString() in constructorParams && isInterface
                    }

                    val copyParams = properties.joinToString(", ") { "${it.simpleName.asString()} = ${it.simpleName.asString()}" }

                    if (delegateProp != null) {
                        val pName = delegateProp.simpleName.asString()
                        writer.write("    is $qName -> this.copy($pName = this.$pName.copyBase($copyParams)) as T\n")
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