package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BOEResponse(
    @SerialName("status")
    val status: Status,
    @SerialName("data")
    val `data`: Data? = null
)