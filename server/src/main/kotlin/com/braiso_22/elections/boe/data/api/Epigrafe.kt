package com.braiso_22.elections.boe.data.api


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Epigrafe(
    @SerialName("item")
    val item: Item
)