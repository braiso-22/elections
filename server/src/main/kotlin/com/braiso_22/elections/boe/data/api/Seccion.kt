package com.braiso_22.elections.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Seccion(
    @SerialName("departamento")
    val departamento: List<Departamento>
)