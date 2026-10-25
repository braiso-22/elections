package com.braiso_22.elections.votacion.infrastructure

import com.braiso_22.elections.identidad.infrastructure.votante
import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.DatoNoValido
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.shared.OpcionDto
import com.braiso_22.elections.shared.VotoDto
import com.braiso_22.elections.shared.VotoRequest
import com.braiso_22.elections.votacion.application.ConsultarMiVoto
import com.braiso_22.elections.votacion.application.Votar
import com.braiso_22.elections.votacion.domain.Opcion
import com.braiso_22.elections.votacion.domain.Voto
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

/** Deben montarse dentro de `authenticate(AUTH_VOTANTE)`: el votante sale del token, nunca del body. */
fun Route.votacionRoutes() {
    val votar by inject<Votar>()
    val consultarMiVoto by inject<ConsultarMiVoto>()

    // PUT porque es idempotente: el mismo votante siempre tiene un único voto por elección.
    put("/elecciones/{id}/voto") {
        val opcion = call.receive<VotoRequest>().toOpcion()
        val voto = votar(EleccionId(call.parameters.getOrFail("id")), call.votante(), opcion)
        call.respond(voto.toDto())
    }
    get("/elecciones/{id}/voto") {
        val voto = consultarMiVoto(EleccionId(call.parameters.getOrFail("id")), call.votante())
        call.respond(voto.toDto())
    }
}

private fun VotoRequest.toOpcion(): Opcion = when (opcion) {
    OpcionDto.CANDIDATURA -> Opcion.ACandidatura(
        CandidaturaId(candidaturaId ?: throw DatoNoValido("Falta candidaturaId para votar a una candidatura"))
    )
    OpcionDto.EN_BLANCO -> Opcion.EnBlanco.also { sinCandidatura() }
    OpcionDto.CONTRA_EL_SISTEMA -> Opcion.ContraElSistema.also { sinCandidatura() }
}

private fun VotoRequest.sinCandidatura() {
    if (candidaturaId != null) throw DatoNoValido("candidaturaId solo se indica al votar a una candidatura")
}

private fun Voto.toDto(): VotoDto {
    val (opcionDto, candidaturaId) = when (val opcion = opcion) {
        is Opcion.ACandidatura -> OpcionDto.CANDIDATURA to opcion.candidaturaId.valor
        Opcion.EnBlanco -> OpcionDto.EN_BLANCO to null
        Opcion.ContraElSistema -> OpcionDto.CONTRA_EL_SISTEMA to null
    }
    return VotoDto(
        eleccionId = eleccionId.valor,
        opcion = opcionDto,
        candidaturaId = candidaturaId,
        emitidoEn = emitidoEn.toString(),
    )
}
