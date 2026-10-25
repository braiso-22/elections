package com.braiso_22.elections.elecciones.infrastructure

import com.braiso_22.elections.elecciones.domain.Candidatura
import com.braiso_22.elections.elecciones.domain.Eleccion
import com.braiso_22.elections.elecciones.domain.PeriodoVotacion
import com.braiso_22.elections.elecciones.domain.ReferenciaBoe
import com.braiso_22.elections.elecciones.domain.RepositorioElecciones
import com.braiso_22.elections.kernel.EleccionId
import java.util.concurrent.ConcurrentHashMap

/**
 * Guarda copias inmutables y reconstruye el agregado en cada lectura, como haría una base de
 * datos: modificar una elección leída no cambia lo guardado hasta llamar a [guardar].
 */
class InMemoryEleccionRepository : RepositorioElecciones {
    private val elecciones = ConcurrentHashMap<EleccionId, Fila>()

    override suspend fun todas(): List<Eleccion> = elecciones.values.map { it.toEleccion() }

    override suspend fun porId(id: EleccionId): Eleccion? = elecciones[id]?.toEleccion()

    override suspend fun porReferencia(referencia: ReferenciaBoe): Eleccion? =
        elecciones.values.firstOrNull { it.referenciaBoe == referencia }?.toEleccion()

    override suspend fun guardar(eleccion: Eleccion) {
        elecciones[eleccion.id] = Fila(
            id = eleccion.id,
            referenciaBoe = eleccion.referenciaBoe,
            nombre = eleccion.nombre,
            periodo = eleccion.periodo,
            candidaturas = eleccion.candidaturas,
        )
    }

    private data class Fila(
        val id: EleccionId,
        val referenciaBoe: ReferenciaBoe,
        val nombre: String,
        val periodo: PeriodoVotacion,
        val candidaturas: List<Candidatura>,
    ) {
        fun toEleccion() = Eleccion.reconstruir(id, referenciaBoe, nombre, periodo, candidaturas)
    }
}
