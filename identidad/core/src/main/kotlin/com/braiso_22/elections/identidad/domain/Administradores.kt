package com.braiso_22.elections.identidad.domain

import com.braiso_22.elections.kernel.AccesoNoPermitido
import com.braiso_22.elections.kernel.VotanteId

/**
 * Quién puede administrar la aplicación. Se guarda aquí y no en el proveedor de identidad
 * (Supabase), que solo dice quién es cada usuario.
 */
interface RegistroDeAdministradores {
    suspend fun esAdministrador(votanteId: VotanteId): Boolean
}

class NoEsAdministrador(votanteId: VotanteId) :
    AccesoNoPermitido("${votanteId.valor} no tiene permisos de administración")
