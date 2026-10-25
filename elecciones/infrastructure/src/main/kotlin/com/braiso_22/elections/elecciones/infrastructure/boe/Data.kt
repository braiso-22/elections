package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("sumario")
    val sumario: Sumario
)