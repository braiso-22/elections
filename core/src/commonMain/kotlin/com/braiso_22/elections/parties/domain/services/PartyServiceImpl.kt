package com.braiso_22.elections.parties.domain.services

import com.braiso_22.elections.parties.domain.entities.PoliticalParty
import com.braiso_22.elections.parties.domain.repositories.PartyRepository

class PartyServiceImpl(
    private val partyRepository: PartyRepository
) : PartyService {

    override suspend fun getAllParties(): List<PoliticalParty> {
        return partyRepository.getAllParties()
    }

    override suspend fun getPartyById(id: String): PoliticalParty? {
        return partyRepository.getPartyById(id)
    }

    override suspend fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty> {
        return partyRepository.getPartiesByParams(id, name, acronym)
    }
}
