plugins {
    id("shared")
    id("com.google.devtools.ksp")
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}

dependencies {
    kspCommonMainMetadata(project(":ksp-processor"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    dependsOn(tasks.matching { it.name == "kspCommonMainKotlinMetadata" })
}