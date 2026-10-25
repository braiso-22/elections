package com.braiso_22.elections

import com.braiso_22.elections.parties.presentation.partiesRouting
import com.braiso_22.elections.parties.presentation.votesRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(sayHello("Ktor"))
        }
    }
    partiesRouting()
    votesRouting()
}
