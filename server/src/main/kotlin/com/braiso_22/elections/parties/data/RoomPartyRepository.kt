package com.braiso_22.elections.parties.data

import com.braiso_22.elections.common.containsSegment
import com.braiso_22.elections.parties.data.room.PartyDao
import com.braiso_22.elections.parties.data.room.toPoliticalParty
import com.braiso_22.elections.parties.domain.entities.PoliticalParty
import com.braiso_22.elections.parties.domain.repositories.PartyRepository

class RoomPartyRepository(
    private val partyDao: PartyDao
) : PartyRepository {

    override suspend fun getAllParties(): List<PoliticalParty> {
        return partyDao.getAll().map { it.toPoliticalParty() }
    }

    override suspend fun getPartyById(id: String): PoliticalParty? {
        return partyDao.getById(id)?.toPoliticalParty()
    }

    // Filtered in Kotlin instead of with LIKE: SQLite's LIKE only ignores case for ASCII
    // and treats % and _ as wildcards, so it wouldn't match containsSegment's behaviour.
    override suspend fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty> {
        return getAllParties().filter {
            it.id == id
                    || name.containsSegment(it.name)
                    || acronym.containsSegment(it.acronym)
        }
    }
}
