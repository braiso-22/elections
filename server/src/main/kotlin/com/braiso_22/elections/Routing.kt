package com.braiso_22.elections

import com.braiso_22.elections.parties.domain.repositories.PartyRepository
import com.braiso_22.elections.parties.domain.repositories.VoteRepository
import com.braiso_22.elections.parties.presentation.partiesRouting
import com.braiso_22.elections.parties.presentation.votesRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    partyRepository: PartyRepository,
    voteRepository: VoteRepository,
) {
    routing {
        get("/") {
            call.respondText(sayHello("Ktor"))
        }
    }
    partiesRouting(partyRepository)
    votesRouting(partyRepository, voteRepository)
}
