package com.braiso_22.parties.presentation.parsers

import com.braiso_22.parties.domain.entities.PoliticalParty
import com.braiso_22.parties.domain.entities.Vote
import com.braiso_22.parties.presentation.dtos.PoliticalPartyDTO
import com.braiso_22.parties.presentation.dtos.VoteDTO

fun PoliticalParty.toDTO(): PoliticalPartyDTO = PoliticalPartyDTO(
    id = id,
    name = name,
    acronym = acronym,
    email = email,
    web = web
)
fun Vote.toDTO(): VoteDTO = VoteDTO(
    partyId = partyId,
    userId
)