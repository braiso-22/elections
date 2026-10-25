package com.braiso_22.elections.parties.data

import com.braiso_22.elections.common.containsSegment
import com.braiso_22.elections.parties.domain.entities.PoliticalParty
import com.braiso_22.elections.parties.domain.repositories.PartyRepository

class PartyRepositoryImpl : PartyRepository {
    private val parties = mutableListOf(
        PoliticalParty(
            "1",
            "Voto en blanco",
            "VB",
            "gobierno@esp.com",
            "https://www.gobiernodeespaña.com/"
        ),
        PoliticalParty(
            "2",
            "Voto nulo / No quiero este sistema",
            "VN",
            "braisfv22@gmail.com",
            "https://braiso-22.github.io/"
        ),
        PoliticalParty(
            "3",
            "Partido test",
            "PT",
            "test@pt.com",
            "http://www.pt.com"
        ),
        PoliticalParty(
            "4",
            "Partido ejemplo",
            "PE",
            "ejemplo@pe.com",
            "http://www.pe.com"
        ),
        PoliticalParty(
            "5",
            "Partido de la Gente",
            "PG",
            "gente@pg.com",
            "http://www.pg.com"
        ),
        PoliticalParty(
            "6",
            "Partido Innovador",
            "PI",
            "innovador@pi.com",
            "http://www.pi.com"
        ),
        PoliticalParty(
            "7",
            "Partido Futuro",
            "PF",
            "futuro@pf.com",
            "http://www.pf.com"
        ),
    )

    override fun getAllParties(): List<PoliticalParty> {
        return parties
    }

    override fun getPartyById(id: String): PoliticalParty? {
        return parties.find { id == it.id }
    }

    override fun getPartiesByParams(id: String, name: String, acronym: String): List<PoliticalParty> {
        return parties.filter {
            it.id == id
                    || name.containsSegment(it.name)
                    || acronym.containsSegment(it.acronym)
        }
    }
}
