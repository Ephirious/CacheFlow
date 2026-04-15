plugins {
    id("data-sql")
    id("data-settings")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.transactions.domain)
            implementation(projects.shared.sync.domain)

            implementation(projects.shared.utils.pure)
        }
    }
}