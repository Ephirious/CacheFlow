plugins {
    id("domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.utils.common)
        }
    }
}