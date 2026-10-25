package com.braiso_22.elections.parties.data

import com.braiso_22.elections.common.containsSegment
import com.braiso_22.elections.parties.domain.entities.PoliticalParty
import com.braiso_22.elections.parties.domain.repositories.PartyRepository

class InMemoryPartyRepository(
    private val parties: List<PoliticalParty> = defaultParties
) : PartyRepository {

    override suspend fun getAllParties(): List<PoliticalParty> {
        return parties
    }

    override suspend fun getPartyById(id: String): PoliticalParty? {
        return parties.find { id == it.id }
    }

    override suspend fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty> {
        return parties.filter {
            it.id == id
                    || name.containsSegment(it.name)
                    || acronym.containsSegment(it.acronym)
        }
    }
}
