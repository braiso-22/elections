package com.braiso_22.elections.resultados.infrastructure

import com.braiso_22.elections.elecciones.application.ObtenerEleccion
import com.braiso_22.elections.elecciones.domain.EleccionNoEncontrada
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.resultados.domain.CandidaturaConocida
import com.braiso_22.elections.resultados.domain.ConsultaCandidaturas

class EleccionesCandidaturasAdapter(private val obtenerEleccion: ObtenerEleccion) : ConsultaCandidaturas {
    override suspend fun candidaturasDe(eleccionId: EleccionId): List<CandidaturaConocida>? {
        val eleccion = try {
            obtenerEleccion(eleccionId)
        } catch (_: EleccionNoEncontrada) {
            return null
        }
        return eleccion.candidaturas.map { CandidaturaConocida(it.id, it.denominacion) }
    }
}
