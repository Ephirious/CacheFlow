plugins {
    id("data-ktor-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.interopSample.domain)
        }
    }
}