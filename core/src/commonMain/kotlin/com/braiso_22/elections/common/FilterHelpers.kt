package com.braiso_22.elections.common

fun String.containsSegment(param: String): Boolean {
    if (param.isEmpty() || this.isEmpty())
        return false
    return param.lowercase().contains(
        this.lowercase()
    )
}
