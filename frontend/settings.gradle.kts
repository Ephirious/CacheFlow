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
include(":k2ts-service") // entryPoint for kotlin service-worker

include(":ksp-processor")

include(":shared:core")
include(":shared:root:presentation")

include(":shared:interop-sample:data")
include(":shared:interop-sample:domain")
include(":shared:interop-sample:presentation")

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

include(":shared:utils:common")
include(":shared:utils:pure")

