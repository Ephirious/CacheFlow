plugins {
    id("data-ktor")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.sync.domain)
            implementation(projects.shared.utils.common)

            implementation(projects.shared.transactions.domain)
            implementation(projects.shared.editors.domain)
        }
    }
}