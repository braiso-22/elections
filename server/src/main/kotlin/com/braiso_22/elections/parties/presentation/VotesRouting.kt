package com.braiso_22.elections.parties.presentation

import com.braiso_22.elections.parties.data.PartyRepositoryImpl
import com.braiso_22.elections.parties.data.VoteRepositoryImpl
import com.braiso_22.elections.parties.domain.services.VoteService
import com.braiso_22.elections.parties.domain.services.VoteServiceImpl
import com.braiso_22.elections.parties.presentation.dtos.VoteDTO
import com.braiso_22.elections.parties.presentation.parsers.toDTO
import com.braiso_22.elections.parties.presentation.parsers.toEntity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.votesRouting() {
    val voteService: VoteService = VoteServiceImpl(
        partyRepository = PartyRepositoryImpl(),
        voteRepository = VoteRepositoryImpl()
    )
    routing {
        route("/v1") {
            post("vote") {
                val vote: VoteDTO = call.receive(VoteDTO::class)
                val result = voteService.vote(
                    vote.toEntity()
                )
                result.onSuccess {
                    call.respond(it.toDTO())
                }.onFailure {
                    call.respondText(
                        text = it.message ?: "Could not vote",
                        status = HttpStatusCode.ExpectationFailed
                    )
                }
            }
            get("votes"){
                val votes = voteService.countVotes()
                call.respond(votes)
            }
        }
    }
}