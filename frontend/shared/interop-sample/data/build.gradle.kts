plugins {
    id("data-ktor")
    id("data-settings")
    id("data-sql")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.interopSample.domain)
            implementation(projects.shared.utils.common)
        }
    }
}