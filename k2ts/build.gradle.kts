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
        }
        jsMain.dependencies {
            // I hate this, but I just copied it to public in webApp....
//            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.2.1"))
//            implementation(npm("sql.js", "1.8.0"))
        }
    }
}
