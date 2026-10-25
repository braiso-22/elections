package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Status(
    @SerialName("code")
    val code: String,
    @SerialName("text")
    val text: String
)