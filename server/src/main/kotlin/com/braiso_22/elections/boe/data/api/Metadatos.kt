package com.braiso_22.elections.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Metadatos(
    @SerialName("fecha_publicacion")
    val fechaPublicacion: Int,
    @SerialName("publicacion")
    val publicacion: String
)