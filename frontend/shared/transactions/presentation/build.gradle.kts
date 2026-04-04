plugins {
    id("presentation")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.shared.transactions.domain)
                implementation(projects.shared.editors.presentation)
                implementation(projects.shared.editors.domain)
            }


            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}

dependencies {
    kspCommonMainMetadata(projects.kspProcessor)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    dependsOn(tasks.matching { it.name == "kspCommonMainKotlinMetadata" })
}


