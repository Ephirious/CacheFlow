import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()
plugins {
    id("shared")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:core"))
            implementation(libs.koin.core)

            // async
            implementation(project(":shared:utils:common"))
            implementation(libs.sqldelight.coroutines)

        }
    }
}