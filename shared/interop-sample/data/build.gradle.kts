plugins {
    id("data-ktor")
    id("data-settings")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.interopSample.domain)
        }
    }
}