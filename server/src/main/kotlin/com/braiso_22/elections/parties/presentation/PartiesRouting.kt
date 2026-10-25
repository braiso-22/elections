package com.braiso_22.elections.parties.presentation

import com.braiso_22.elections.boe.data.api.BOEResponse
import com.braiso_22.elections.boe.data.api.getBoe
import com.braiso_22.elections.parties.domain.repositories.PartyRepository
import com.braiso_22.elections.parties.domain.services.PartyService
import com.braiso_22.elections.parties.domain.services.PartyServiceImpl
import com.braiso_22.elections.parties.presentation.dtos.PoliticalPartyDTO
import com.braiso_22.elections.parties.presentation.parsers.toDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.partiesRouting(partyRepository: PartyRepository) {
    val partyService: PartyService = PartyServiceImpl(
        partyRepository = partyRepository
    )
    routing {
        route("/v1") {
            get("/parties") {
                val queryParams = call.queryParameters

                val id: String = queryParams["id"] ?: ""
                val name: String = queryParams["name"] ?: ""
                val acronym: String = queryParams["acronym"] ?: ""

                if (id.isBlank() && name.isBlank() && acronym.isBlank()) {
                    val parties: List<PoliticalPartyDTO> = partyService.getAllParties().map {
                        it.toDTO()
                    }
                    call.respond(parties)
                    return@get
                }

                val party: List<PoliticalPartyDTO> = partyService.getPartiesByParams(
                    id, name, acronym
                ).map {
                    it.toDTO()
                }
                call.respond(party)
            }
            get("/party/{id}") {
                val id: String = call.parameters["id"] ?: ""

                val party: PoliticalPartyDTO? = partyService.getPartyById(id)?.toDTO()
                party?.let {
                    call.respond(it)
                } ?: call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "Party not found"
                )
            }
            get("/boe-parties") {
                val boe: BOEResponse? = getBoe()
                boe?.let { call.respond(it) } ?: call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "No data"
                )
            }
        }
    }
}
