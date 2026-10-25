package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UrlPdf(
    @SerialName("texto")
    val texto: String,
    @SerialName("szBytes")
    val szBytes: String? = null,
    @SerialName("szKBytes")
    val szKBytes: String? = null,
    @SerialName("pagina_inicial")
    val paginaInicial: String? = null,
    @SerialName("pagina_final")
    val paginaFinal: String? = null
)