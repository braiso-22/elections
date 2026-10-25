package com.braiso_22.elections

import com.braiso_22.elections.elecciones.infrastructure.eleccionesAdminRoutes
import com.braiso_22.elections.elecciones.infrastructure.eleccionesRoutes
import com.braiso_22.elections.identidad.infrastructure.AUTH_VOTANTE
import com.braiso_22.elections.identidad.infrastructure.soloAdministradores
import com.braiso_22.elections.partidos.infrastructure.partidosAdminRoutes
import com.braiso_22.elections.partidos.infrastructure.partidosRoutes
import com.braiso_22.elections.resultados.infrastructure.resultadosRoutes
import com.braiso_22.elections.votacion.infrastructure.votacionRoutes
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configurarRutas() {
    routing {
        get("/health") { call.respondText("OK") }

        route("/v1") {
            partidosRoutes()
            eleccionesRoutes()
            resultadosRoutes()

            authenticate(AUTH_VOTANTE) {
                votacionRoutes()
            }

            route("/admin") {
                soloAdministradores {
                    partidosAdminRoutes()
                    eleccionesAdminRoutes()
                }
            }
        }
    }
}
