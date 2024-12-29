package com.braiso_22.parties.domain.services

import com.braiso_22.parties.domain.entities.PoliticalParty
import com.braiso_22.parties.domain.repositories.PartyRepository

class PartyServiceImpl(
    private val partyRepository: PartyRepository
) : PartyService {

    override fun getAllParties(): List<PoliticalParty> {
        return partyRepository.getAllParties()
    }

    override fun getPartyById(id: String): PoliticalParty? {
        return partyRepository.getPartyById(id)
    }

    override fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty> {
        return partyRepository.getPartiesByParams(id, name, acronym)
    }
}
