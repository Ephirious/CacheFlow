plugins {
    id("data-settings")
    id("data-ktor")
}

kotlin {
    sourceSets {
        commonMain.dependencies {

            api(projects.shared.auth.domain)
            api(projects.shared.sync.domain)


            implementation(libs.ktor.client.auth)


            implementation(projects.shared.utils.common)
        }
    }
}