package com.braiso_22.elections.resultados.infrastructure

import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.resultados.application.Cifra
import com.braiso_22.elections.resultados.application.ConsultarResultados
import com.braiso_22.elections.resultados.application.Resultados
import com.braiso_22.elections.shared.CifraDto
import com.braiso_22.elections.shared.ResultadoCandidaturaDto
import com.braiso_22.elections.shared.ResultadosDto
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Route.resultadosRoutes() {
    val consultarResultados by inject<ConsultarResultados>()

    get("/elecciones/{id}/resultados") {
        call.respond(consultarResultados(EleccionId(call.parameters.getOrFail("id"))).toDto())
    }
}

private fun Resultados.toDto() = ResultadosDto(
    eleccionId = eleccionId.valor,
    participacion = participacion,
    enBlanco = enBlanco.toDto(),
    contraElSistema = contraElSistema.toDto(),
    candidaturas = candidaturas.map {
        ResultadoCandidaturaDto(candidaturaId = it.candidaturaId.valor, denominacion = it.denominacion, cifra = it.cifra.toDto())
    }.toTypedArray(),
)

private fun Cifra.toDto() = CifraDto(votos = votos, porcentaje = porcentaje)
