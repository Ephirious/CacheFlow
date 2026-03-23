import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()
plugins {
    id("shared")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // for flow
            implementation(libs.kotlinx.coroutines)

            // bigDecimal
            implementation(project(":shared:utils:pure"))
        }
    }
}