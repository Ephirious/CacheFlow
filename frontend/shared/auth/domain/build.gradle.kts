plugins {
    id("domain")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            // for KtorAuthPlugin
            implementation(libs.ktor.client.core)

            implementation(projects.shared.sync.domain)
            implementation(projects.shared.transactions.domain)

            implementation(projects.shared.coreValidation)
        }
    }
}