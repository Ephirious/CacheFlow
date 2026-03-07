plugins {
    id("shared-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.decompose.core)
            implementation(libs.flowmvi.core)
            implementation(libs.flowmvi.essenty)

            implementation(libs.kotlinx.coroutines)

        }
    }
}

