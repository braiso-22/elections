package com.braiso_22.elections.parties.domain.services

import com.braiso_22.elections.parties.domain.entities.PoliticalParty

interface PartyService {
    suspend fun getAllParties(): List<PoliticalParty>
    suspend fun getPartyById(id: String): PoliticalParty?
    suspend fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty>
}