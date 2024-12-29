package com.braiso_22.common

import java.util.*

fun String.containsSegment(param: String): Boolean {
    if (param.isEmpty() || this.isEmpty())
        return false
    return param.lowercase(Locale.getDefault()).contains(
        this.lowercase(Locale.getDefault())
    )
}