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

include(":k2ts") // umbrella module for ts

include(":shared:core")
include(":shared:root:presentation")

include(":shared:interop-sample:data")
include(":shared:interop-sample:domain")
include(":shared:interop-sample:presentation")

include(":shared:transactions:data")
include(":shared:transactions:domain")
include(":shared:transactions:presentation")

include(":shared:stats:presentation")

include(":shared:more:data")
include(":shared:more:domain")
include(":shared:more:presentation")

include(":shared:editors:data")
include(":shared:editors:domain")
include(":shared:editors:presentation")

include(":shared:sync:data")
include(":shared:sync:domain")

include(":shared:utils:common")

