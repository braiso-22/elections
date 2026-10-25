package com.braiso_22.elections

import com.braiso_22.elections.identidad.infrastructure.votantesDeDesarrollo
import com.braiso_22.elections.kernel.AccesoNoPermitido
import com.braiso_22.elections.kernel.DatoNoValido
import com.braiso_22.elections.kernel.ErrorDeDominio
import com.braiso_22.elections.kernel.OperacionNoPermitida
import com.braiso_22.elections.kernel.RecursoNoEncontrado
import com.braiso_22.elections.shared.ErrorDto
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configurarSerializacion() {
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging)
}

/** Traduce las categorías de error de dominio a HTTP; los contextos no saben nada de status codes. */
fun Application.configurarErrores() {
    install(StatusPages) {
        exception<RecursoNoEncontrado> { call, error -> call.responderError(HttpStatusCode.NotFound, error) }
        exception<OperacionNoPermitida> { call, error -> call.responderError(HttpStatusCode.Conflict, error) }
        exception<DatoNoValido> { call, error -> call.responderError(HttpStatusCode.UnprocessableEntity, error) }
        exception<AccesoNoPermitido> { call, error -> call.responderError(HttpStatusCode.Forbidden, error) }
        exception<BadRequestException> { call, error ->
            call.respond(HttpStatusCode.BadRequest, ErrorDto("PeticionMalFormada", error.message ?: "Petición mal formada"))
        }
    }
}

private suspend fun ApplicationCall.responderError(status: HttpStatusCode, error: ErrorDeDominio) =
    respond(status, ErrorDto(codigo = error::class.simpleName ?: "ErrorDeDominio", mensaje = error.message.orEmpty()))

fun Application.configurarSeguridad() {
    install(Authentication) {
        votantesDeDesarrollo()
    }
}
