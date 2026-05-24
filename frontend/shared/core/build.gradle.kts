plugins {
    id("shared")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)

            implementation(libs.bundles.core.ktor.client)

            implementation(libs.bundles.settings)
            implementation(projects.shared.utils.pure)


            // install auth feature (refresh tokens)
            implementation(projects.shared.auth.domain)
        }
        jsMain.dependencies {
            implementation(libs.sqldelight.web.worker)
            // k2ts implements other devNpm, etc. files!!
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName = Config.namespace + ".db"
            generateAsync.set(true)
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:${libs.versions.sqldelight.get()}")
        }
    }
}