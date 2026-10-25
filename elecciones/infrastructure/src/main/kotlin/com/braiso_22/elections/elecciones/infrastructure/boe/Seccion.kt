package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Seccion(
    @SerialName("codigo")
    val codigo: String,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("departamento")
    @Serializable(with = DepartamentoListSerializer::class)
    val departamento: List<Departamento>
)