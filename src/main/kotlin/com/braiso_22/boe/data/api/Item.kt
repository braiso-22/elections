package com.braiso_22.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("control")
    val control: String,
    @SerialName("identificador")
    val identificador: String,
    @SerialName("titulo")
    val titulo: String,
    @SerialName("url_html")
    val urlHtml: String,
    @SerialName("url_pdf")
    val urlPdf: String,
    @SerialName("url_xml")
    val urlXml: String
)