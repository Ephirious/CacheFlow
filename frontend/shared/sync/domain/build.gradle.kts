

plugins {
    id("domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.utils.common)

            // for SyncStatus serialization (used)
            implementation(libs.bundles.serialization)
        }
    }
}