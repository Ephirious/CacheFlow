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
            freeCompilerArgs.addAll(listOf("-Xir-per-module=false"))
        }
        useEsModules()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.root.presentation)
            implementation(projects.shared.utils.common)
            implementation(projects.shared.sync.domain)
            implementation(projects.shared.sync.data)
            implementation(libs.koin.core)
        }
    }
}
