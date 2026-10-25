package com.braiso_22.elections.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Status(
    @SerialName("code")
    val code: Int,
    @SerialName("text")
    val text: String
)