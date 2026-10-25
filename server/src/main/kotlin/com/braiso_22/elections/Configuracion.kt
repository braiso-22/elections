package com.braiso_22.elections

import com.braiso_22.elections.kernel.VotanteId
import kotlin.time.Clock

data class Configuracion(
    val puerto: Int,
    /** Votantes con permisos de administración, hasta que se gestionen desde la aplicación. */
    val administradores: Set<VotanteId>,
    val reloj: Clock,
) {
    companion object {
        fun desdeEntorno() = Configuracion(
            puerto = System.getenv("PORT")?.toInt() ?: 8080,
            administradores = System.getenv("ELECTIONS_ADMINS").orEmpty()
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map(::VotanteId)
                .toSet(),
            reloj = Clock.System,
        )
    }
}
