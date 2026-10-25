package com.braiso_22.elections.parties.presentation.parsers

import com.braiso_22.elections.parties.domain.entities.Vote
import com.braiso_22.elections.parties.presentation.dtos.VoteDTO

fun VoteDTO.toEntity(): Vote = Vote(
    partyId = partyId,
    userId = userId
)