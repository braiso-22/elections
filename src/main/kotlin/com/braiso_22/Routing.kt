package com.braiso_22

import com.braiso_22.parties.presentation.partiesRouting
import com.braiso_22.parties.presentation.votesRouting
import io.ktor.server.application.*

fun Application.configureRouting() {
    partiesRouting()
    votesRouting()
}
