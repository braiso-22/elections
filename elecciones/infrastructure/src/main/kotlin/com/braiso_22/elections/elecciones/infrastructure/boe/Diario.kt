package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Diario(
    @SerialName("numero")
    val numero: String,
    @SerialName("seccion")
    @Serializable(with = SeccionListSerializer::class)
    val seccion: List<Seccion>,
    @SerialName("sumario_diario")
    val sumarioDiario: SumarioDiario
)