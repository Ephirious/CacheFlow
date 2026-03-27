plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.sqldelight) apply false
}

tasks.register("buildK2ts") {
    group = "k2ts"
    description = "Сборка клиента"

    dependsOn(":k2ts:jsBrowserProductionLibraryDistribution")

    doLast {
        println("Собран клиент!!")
    }
}