package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Metadatos(
    @SerialName("fecha_publicacion")
    val fechaPublicacion: String,
    @SerialName("publicacion")
    val publicacion: String
)