plugins {
    id("presentation")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.transactions.domain)
            implementation(projects.shared.editors.presentation)
            implementation(projects.shared.editors.domain)
        }
    }
}