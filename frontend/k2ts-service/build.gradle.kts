plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        outputModuleName = "k2ts-service"
        browser()
        binaries.library()
        compilerOptions {
            target = "es2015"
            freeCompilerArgs.addAll(listOf("-Xir-per-module=false"))
        }
        useEsModules()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)
            implementation(projects.shared.sync.domain)
            implementation(projects.shared.sync.data)
            implementation(libs.koin.core)

            implementation(projects.shared.interopSample.domain)
            implementation(projects.shared.interopSample.data)

            implementation(projects.shared.utils.pure)
        }
    }
}
