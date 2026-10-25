package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Departamento(
    @SerialName("codigo")
    val codigo: String,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("epigrafe")
    @Serializable(with = EpigrafeListSerializer::class)
    val epigrafe: List<Epigrafe> = emptyList(),
    @SerialName("item")
    @Serializable(with = ItemListSerializer::class)
    val item: List<Item> = emptyList(),
    @SerialName("texto")
    val texto: Texto? = null
)