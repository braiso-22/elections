package com.braiso_22.elections.elecciones.infrastructure

import com.braiso_22.elections.elecciones.application.ImportarEleccionDelBoe
import com.braiso_22.elections.elecciones.application.ListarElecciones
import com.braiso_22.elections.elecciones.application.ObtenerEleccion
import com.braiso_22.elections.elecciones.application.VistaEleccion
import com.braiso_22.elections.elecciones.domain.EstadoEleccion
import com.braiso_22.elections.elecciones.domain.PeriodoVotacion
import com.braiso_22.elections.elecciones.domain.ReferenciaBoe
import com.braiso_22.elections.kernel.DatoNoValido
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.shared.CandidaturaDto
import com.braiso_22.elections.shared.EleccionDto
import com.braiso_22.elections.shared.EleccionResumenDto
import com.braiso_22.elections.shared.EstadoEleccionDto
import com.braiso_22.elections.shared.ImportarEleccionRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject
import kotlin.time.Instant

fun Route.eleccionesRoutes() {
    val listarElecciones by inject<ListarElecciones>()
    val obtenerEleccion by inject<ObtenerEleccion>()

    get("/elecciones") {
        call.respond(listarElecciones().map { it.toResumenDto() })
    }
    get("/elecciones/{id}") {
        call.respond(obtenerEleccion(EleccionId(call.parameters.getOrFail("id"))).toDto())
    }
}

/** Rutas de administración. Quien las monta decide cómo se protegen. */
fun Route.eleccionesAdminRoutes() {
    val importarEleccion by inject<ImportarEleccionDelBoe>()

    post("/importaciones/boe") {
        val request = call.receive<ImportarEleccionRequest>()
        val resultado = importarEleccion(
            referencia = ReferenciaBoe(request.referenciaBoe),
            periodo = PeriodoVotacion(instante(request.inicioVotacion), instante(request.finVotacion)),
        )
        val status = if (resultado.creada) HttpStatusCode.Created else HttpStatusCode.OK
        call.respond(status, resultado.eleccion.toDto())
    }
}

private fun instante(texto: String): Instant =
    runCatching { Instant.parse(texto) }.getOrElse { throw DatoNoValido("Fecha no válida: '$texto'. Usa ISO-8601, p. ej. 2027-05-23T07:00:00Z") }

private fun EstadoEleccion.toDto() = when (this) {
    EstadoEleccion.CONVOCADA -> EstadoEleccionDto.CONVOCADA
    EstadoEleccion.ABIERTA -> EstadoEleccionDto.ABIERTA
    EstadoEleccion.CERRADA -> EstadoEleccionDto.CERRADA
}

private fun VistaEleccion.toResumenDto() = EleccionResumenDto(
    id = id.valor,
    nombre = nombre,
    estado = estado.toDto(),
    inicioVotacion = periodo.inicio.toString(),
    finVotacion = periodo.fin.toString(),
)

private fun VistaEleccion.toDto() = EleccionDto(
    id = id.valor,
    referenciaBoe = referenciaBoe.valor,
    nombre = nombre,
    estado = estado.toDto(),
    inicioVotacion = periodo.inicio.toString(),
    finVotacion = periodo.fin.toString(),
    candidaturas = candidaturas.map {
        CandidaturaDto(id = it.id.valor, partidoId = it.partidoId.valor, denominacion = it.denominacion)
    }.toTypedArray(),
)
