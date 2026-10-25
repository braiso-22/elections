package com.braiso_22.elections.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sumario(
    @SerialName("diario")
    val diario: Diario,
    @SerialName("metadatos")
    val metadatos: Metadatos
)