plugins {
    id("data-sql")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.transactions.domain)
            implementation(projects.shared.sync.domain)
        }
    }
}