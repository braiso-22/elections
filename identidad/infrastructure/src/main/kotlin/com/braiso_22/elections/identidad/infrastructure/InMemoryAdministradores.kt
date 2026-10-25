package com.braiso_22.elections.identidad.infrastructure

import com.braiso_22.elections.identidad.domain.RegistroDeAdministradores
import com.braiso_22.elections.kernel.VotanteId

/** Administradores fijados por configuración hasta tener su tabla en Postgres. */
class InMemoryAdministradores(private val administradores: Set<VotanteId>) : RegistroDeAdministradores {
    override suspend fun esAdministrador(votanteId: VotanteId): Boolean = votanteId in administradores
}
