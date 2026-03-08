import org.gradle.accessors.dm.LibrariesForLibs
val libs = the<LibrariesForLibs>()
plugins {
    id("shared")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.settings)
        }
    }
}