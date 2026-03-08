import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("shared")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.presentation.api)

            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines)


            implementation(project(":shared:utils:common")) // projects.shared.utils.common
        }
    }
}