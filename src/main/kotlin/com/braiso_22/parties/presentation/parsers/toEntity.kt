package com.braiso_22.parties.presentation.parsers

import com.braiso_22.parties.domain.entities.Vote
import com.braiso_22.parties.presentation.dtos.VoteDTO

fun VoteDTO.toEntity(): Vote = Vote(
    partyId = partyId,
    userId = userId
)