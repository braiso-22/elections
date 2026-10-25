package com.braiso_22.elections.partidos.infrastructure

import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.partidos.application.ImportarPartidosDelRegistro
import com.braiso_22.elections.partidos.application.ListarPartidos
import com.braiso_22.elections.partidos.application.ObtenerPartido
import com.braiso_22.elections.partidos.domain.Partido
import com.braiso_22.elections.shared.ImportacionPartidosDto
import com.braiso_22.elections.shared.PartidoDto
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Route.partidosRoutes() {
    val listarPartidos by inject<ListarPartidos>()
    val obtenerPartido by inject<ObtenerPartido>()

    get("/partidos") {
        val partidos = listarPartidos(busqueda = call.queryParameters["busqueda"])
        call.respond(partidos.map { it.toDto() })
    }
    get("/partidos/{id}") {
        val partido = obtenerPartido(PartidoId(call.parameters.getOrFail("id")))
        call.respond(partido.toDto())
    }
}

/** Rutas de administración. Quien las monta decide cómo se protegen. */
fun Route.partidosAdminRoutes() {
    val importarPartidos by inject<ImportarPartidosDelRegistro>()

    post("/importaciones/registro-partidos") {
        val resultado = importarPartidos()
        call.respond(ImportacionPartidosDto(nuevos = resultado.nuevos, actualizados = resultado.actualizados))
    }
}

private fun Partido.toDto() = PartidoDto(id = id.valor, nombre = nombre, siglas = siglas.valor)
