plugins {
    id("presentation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.more.domain)
            implementation(projects.shared.editors.domain)
        }
    }
}