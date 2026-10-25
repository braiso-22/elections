package com.braiso_22.elections

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform