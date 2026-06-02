rootProject.name = "CacheFlow"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":k2ts") // entryPoint for ts-kotlin

include(":ksp-processor")

include(":shared:core-validation")

include(":shared:core")
include(":shared:root:presentation")

include(":shared:transactions:data")
include(":shared:transactions:domain")
include(":shared:transactions:presentation")

include(":shared:stats:presentation")

include(":shared:settings:data")
include(":shared:settings:domain")
include(":shared:settings:presentation")

include(":shared:editors:data")
include(":shared:editors:domain")
include(":shared:editors:presentation")

include(":shared:sync:data")
include(":shared:sync:domain")

include(":shared:auth:data")
include(":shared:auth:domain")
include(":shared:auth:presentation")

include(":shared:utils:common")
include(":shared:utils:pure")

