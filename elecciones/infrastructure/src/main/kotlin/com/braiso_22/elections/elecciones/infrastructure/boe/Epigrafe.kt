package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Epigrafe(
    @SerialName("nombre")
    val nombre: String,
    @SerialName("item")
    @Serializable(with = ItemListSerializer::class)
    val item: List<Item>
)