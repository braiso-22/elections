package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SumarioDiario(
    @SerialName("identificador")
    val identificador: String,
    @SerialName("url_pdf")
    val urlPdf: UrlPdf
)