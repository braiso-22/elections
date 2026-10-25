package com.braiso_22.elections.identidad.application

import com.braiso_22.elections.identidad.domain.NoEsAdministrador
import com.braiso_22.elections.identidad.domain.RegistroDeAdministradores
import com.braiso_22.elections.kernel.VotanteId

class AutorizarAdministracion(private val administradores: RegistroDeAdministradores) {
    /** @throws NoEsAdministrador */
    suspend operator fun invoke(votanteId: VotanteId) {
        if (!administradores.esAdministrador(votanteId)) throw NoEsAdministrador(votanteId)
    }
}
