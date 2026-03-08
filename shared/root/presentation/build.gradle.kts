plugins {
    id("presentation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)

            implementation(projects.shared.interopSample.data)
            implementation(projects.shared.interopSample.presentation)
        }
    }
}