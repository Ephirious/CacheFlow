plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.sqldelight) apply false
}

tasks.register("buildAll") {
    group = "k2ts"
    description = "Сборка клиента и ServiceWorker"

    dependsOn(":k2ts:jsBrowserProductionLibraryDistribution")
    dependsOn(":k2ts-service:jsBrowserDistribution")

    doLast {
        println("Собраны клиент и ServiceWorker!!")
    }
}

tasks.register("buildClient") {
    group = "k2ts"
    description = "Сборка клиента"

    dependsOn(":k2ts:jsBrowserProductionLibraryDistribution")

    doLast {
        println("Собран клиент!!")
    }
}

tasks.register("buildService") {
    group = "k2ts"
    description = "Сборка ServiceWorker"

    dependsOn(":k2ts-service:jsBrowserDistribution")

    doLast {
        println("Собран ServiceWorker!!")
    }
}