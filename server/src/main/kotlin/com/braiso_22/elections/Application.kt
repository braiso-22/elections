package com.braiso_22.elections

import com.braiso_22.elections.elecciones.infrastructure.eleccionesModule
import com.braiso_22.elections.identidad.infrastructure.identidadModule
import com.braiso_22.elections.kernel.BusDeEventos
import com.braiso_22.elections.kernel.BusDeEventosEnProceso
import com.braiso_22.elections.partidos.infrastructure.partidosModule
import com.braiso_22.elections.resultados.infrastructure.SuscriptorDeVotacion
import com.braiso_22.elections.resultados.infrastructure.resultadosModule
import com.braiso_22.elections.votacion.infrastructure.votacionModule
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlin.time.Clock

fun main() {
    val configuracion = Configuracion.desdeEntorno()
    embeddedServer(Netty, port = configuracion.puerto, host = "0.0.0.0") { module(configuracion) }
        .start(wait = true)
}

/** Composition root: el único sitio que conoce todos los contextos y cómo se conectan. */
fun Application.module(configuracion: Configuracion) {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<Clock> { configuracion.reloj }
                single<BusDeEventos> { BusDeEventosEnProceso() }
            },
            partidosModule,
            eleccionesModule,
            votacionModule,
            resultadosModule,
            identidadModule(configuracion.administradores),
        )
    }
    get<SuscriptorDeVotacion>().suscribir()

    configurarSerializacion()
    configurarErrores()
    configurarSeguridad()
    configurarRutas()
}
