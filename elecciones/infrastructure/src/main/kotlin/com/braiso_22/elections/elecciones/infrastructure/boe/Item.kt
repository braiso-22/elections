package com.braiso_22.elections.elecciones.infrastructure.boe


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("control")
    val control: String? = null,
    @SerialName("identificador")
    val identificador: String,
    @SerialName("titulo")
    val titulo: String,
    @SerialName("url_html")
    val urlHtml: String? = null,
    @SerialName("url_pdf")
    val urlPdf: UrlPdf? = null,
    @SerialName("url_xml")
    val urlXml: String? = null
)