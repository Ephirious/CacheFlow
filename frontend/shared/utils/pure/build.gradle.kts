plugins {
    id("shared")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }

        webMain.dependencies {
            implementation(npm("big.js", "^7.0.1"))
            implementation(npm("graphemer", "1.4.0"))
        }
    }
}