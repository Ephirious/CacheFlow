plugins {
    id("data-settings")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.settings.domain)
        }
    }
}