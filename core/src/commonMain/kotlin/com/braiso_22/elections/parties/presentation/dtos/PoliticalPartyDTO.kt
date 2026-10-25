package com.braiso_22.elections.parties.presentation.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PoliticalPartyDTO(
    val id: String,
    val name: String,
    val acronym: String,
    val email: String,
    val web: String,
)
