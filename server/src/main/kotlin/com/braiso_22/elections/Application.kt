package com.braiso_22.elections

import com.braiso_22.elections.parties.data.RoomPartyRepository
import com.braiso_22.elections.parties.data.RoomVoteRepository
import com.braiso_22.elections.parties.data.room.createElectionsDatabase
import com.braiso_22.elections.parties.domain.repositories.PartyRepository
import com.braiso_22.elections.parties.domain.repositories.VoteRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val database = createElectionsDatabase(
        path = System.getenv("ELECTIONS_DB_PATH") ?: "elections.db"
    )
    monitor.subscribe(ApplicationStopped) { database.close() }

    module(
        partyRepository = RoomPartyRepository(database.partyDao()),
        voteRepository = RoomVoteRepository(database.voteDao()),
    )
}

fun Application.module(
    partyRepository: PartyRepository,
    voteRepository: VoteRepository,
) {
    configureSerialization()
    configureSecurity()
    configureRouting(partyRepository, voteRepository)
}
