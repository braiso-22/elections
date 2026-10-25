package com.braiso_22.elections.elecciones.domain

import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.PartidoId

interface RepositorioElecciones {
    suspend fun todas(): List<Eleccion>
    suspend fun porId(id: EleccionId): Eleccion?
    suspend fun porReferencia(referencia: ReferenciaBoe): Eleccion?
    suspend fun guardar(eleccion: Eleccion)
}

/** Boletín Oficial del Estado: de él salen la convocatoria y las candidaturas proclamadas. */
interface BoletinOficial {
    suspend fun convocatoria(referencia: ReferenciaBoe): ConvocatoriaPublicada?
}

data class ConvocatoriaPublicada(
    val referencia: ReferenciaBoe,
    val nombre: String,
    val candidaturas: List<CandidaturaPublicada>,
)

/** El BOE identifica a los partidos por sus siglas, no por ningún id nuestro. */
data class CandidaturaPublicada(val siglas: String, val denominacion: String)

/** Lo que Elecciones necesita del contexto Partidos. */
interface CatalogoDePartidos {
    suspend fun partidoConSiglas(siglas: String): PartidoId?
}
