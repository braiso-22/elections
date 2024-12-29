package com.braiso_22.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SumarioDiario(
    @SerialName("identificador")
    val identificador: String,
    @SerialName("url_pdf")
    val urlPdf: String
)