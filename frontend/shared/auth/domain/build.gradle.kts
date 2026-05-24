plugins {
    id("domain")
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            // for KtorAuthPlugin
            implementation(libs.ktor.client.core)
        }
    }
}