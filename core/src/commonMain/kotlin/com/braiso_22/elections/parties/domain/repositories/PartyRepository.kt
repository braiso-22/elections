package com.braiso_22.elections.parties.domain.repositories

import com.braiso_22.elections.parties.domain.entities.PoliticalParty

interface PartyRepository {
    suspend fun getAllParties(): List<PoliticalParty>
    suspend fun getPartyById(id: String): PoliticalParty?
    suspend fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty>
}