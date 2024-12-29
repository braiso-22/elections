package com.braiso_22.parties.presentation.dtos

import kotlinx.serialization.Serializable

@Serializable
data class VoteDTO(
    val partyId: String,
    val userId: String,
)
