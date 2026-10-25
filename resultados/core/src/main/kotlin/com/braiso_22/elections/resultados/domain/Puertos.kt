package com.braiso_22.elections.resultados.domain

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.RecursoNoEncontrado

interface RepositorioRecuentos {
    /** null si todavía no se ha contado ningún voto. */
    suspend fun de(eleccionId: EleccionId): Recuento?

    /** Aplica [cambio] de forma atómica, partiendo de un recuento vacío si no había. */
    suspend fun actualizar(eleccionId: EleccionId, cambio: (Recuento) -> Recuento)
}

/** Lo que Resultados necesita del contexto Elecciones: qué candidaturas hay y cómo se llaman. */
interface ConsultaCandidaturas {
    /** null si la elección no existe. */
    suspend fun candidaturasDe(eleccionId: EleccionId): List<CandidaturaConocida>?
}

data class CandidaturaConocida(val id: CandidaturaId, val denominacion: String)

class EleccionSinResultados(id: EleccionId) : RecursoNoEncontrado("No existe la elección ${id.valor}")
