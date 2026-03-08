plugins {
    id("shared")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.bundles.core.ktor.client)
            implementation(libs.bundles.settings)
        }

        jsMain.dependencies {
            implementation(libs.sqldelight.web)
            // k2ts implements other devNpm, etc. files!!
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName = Config.namespace+".db"
            generateAsync.set(true)
        }
    }
}