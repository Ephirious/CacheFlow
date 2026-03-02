package org.cacheflow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform