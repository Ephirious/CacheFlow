plugins {
    id("domain")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.shared.editors.domain)
        }
    }
}