package com.braiso_22.elections.votacion.infrastructure

import com.braiso_22.elections.elecciones.application.ObtenerEleccion
import com.braiso_22.elections.elecciones.domain.EleccionNoEncontrada
import com.braiso_22.elections.elecciones.domain.EstadoEleccion
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.votacion.domain.ConsultaElecciones
import com.braiso_22.elections.votacion.domain.SituacionEleccion

/** Traduce el modelo del contexto Elecciones a lo poco que Votación necesita de él. */
class EleccionesConsultaAdapter(private val obtenerEleccion: ObtenerEleccion) : ConsultaElecciones {
    override suspend fun situacion(eleccionId: EleccionId): SituacionEleccion? {
        val eleccion = try {
            obtenerEleccion(eleccionId)
        } catch (_: EleccionNoEncontrada) {
            return null
        }
        return SituacionEleccion(
            admiteVotos = eleccion.estado == EstadoEleccion.ABIERTA,
            candidaturas = eleccion.candidaturas.map { it.id }.toSet(),
        )
    }
}
