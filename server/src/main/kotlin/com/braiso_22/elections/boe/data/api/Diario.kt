package com.braiso_22.elections.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Diario(
    @SerialName("seccion")
    val seccion: List<Seccion>,
    @SerialName("sumario_diario")
    val sumarioDiario: SumarioDiario
)