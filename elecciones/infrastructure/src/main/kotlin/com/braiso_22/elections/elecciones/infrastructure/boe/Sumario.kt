package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sumario(
    @SerialName("diario")
    @Serializable(with = DiarioListSerializer::class)
    val diario: List<Diario>,
    @SerialName("metadatos")
    val metadatos: Metadatos
)