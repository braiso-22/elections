package com.braiso_22.elections.parties.domain.repositories

import com.braiso_22.elections.parties.domain.entities.PoliticalParty

interface PartyRepository {
    fun getAllParties(): List<PoliticalParty>
    fun getPartyById(id: String): PoliticalParty?
    fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty>
}