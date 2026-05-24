plugins {
    id("presentation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.settings.domain)
            implementation(projects.shared.editors.domain)
            implementation(projects.shared.editors.presentation)
            implementation(projects.shared.auth.presentation)
        }
    }
}