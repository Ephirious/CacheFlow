plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        outputModuleName = "k2ts"
        browser()
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
            freeCompilerArgs.addAll(listOf("-Xir-per-module=false",
                "-Xir-property-lazy-initialization"))
        }

        useEsModules()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.root.presentation)
            implementation(projects.shared.utils.common)
            implementation(projects.shared.sync.domain)
            implementation(projects.shared.sync.data)

            implementation(projects.shared.settings.domain)

            implementation(libs.koin.core)
        }
    }
}
