plugins {
    id("data-ktor")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
            implementation(projects.shared.sync.domain)
            implementation(projects.shared.utils.common)

            implementation(projects.shared.transactions.domain)
            implementation(projects.shared.editors.domain)
        }
    }
}