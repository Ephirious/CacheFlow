plugins {
    id("data-sql")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.editors.domain)
            implementation(projects.shared.sync.domain)
        }
    }
}