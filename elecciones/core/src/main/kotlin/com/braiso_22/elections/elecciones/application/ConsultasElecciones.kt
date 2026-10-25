package com.braiso_22.elections.elecciones.application

import com.braiso_22.elections.elecciones.domain.Candidatura
import com.braiso_22.elections.elecciones.domain.Eleccion
import com.braiso_22.elections.elecciones.domain.EleccionNoEncontrada
import com.braiso_22.elections.elecciones.domain.EstadoEleccion
import com.braiso_22.elections.elecciones.domain.PeriodoVotacion
import com.braiso_22.elections.elecciones.domain.ReferenciaBoe
import com.braiso_22.elections.elecciones.domain.RepositorioElecciones
import com.braiso_22.elections.kernel.EleccionId
import kotlin.time.Clock
import kotlin.time.Instant

/** Una elección tal y como está en un instante dado. */
data class VistaEleccion(
    val id: EleccionId,
    val referenciaBoe: ReferenciaBoe,
    val nombre: String,
    val periodo: PeriodoVotacion,
    val estado: EstadoEleccion,
    val candidaturas: List<Candidatura>,
)

internal fun Eleccion.vistaEn(ahora: Instant) = VistaEleccion(
    id = id,
    referenciaBoe = referenciaBoe,
    nombre = nombre,
    periodo = periodo,
    estado = estadoEn(ahora),
    candidaturas = candidaturas,
)

class ListarElecciones(
    private val elecciones: RepositorioElecciones,
    private val reloj: Clock,
) {
    /** De la más reciente a la más antigua. */
    suspend operator fun invoke(): List<VistaEleccion> {
        val ahora = reloj.now()
        return elecciones.todas()
            .sortedByDescending { it.periodo.inicio }
            .map { it.vistaEn(ahora) }
    }
}

class ObtenerEleccion(
    private val elecciones: RepositorioElecciones,
    private val reloj: Clock,
) {
    suspend operator fun invoke(id: EleccionId): VistaEleccion =
        elecciones.porId(id)?.vistaEn(reloj.now()) ?: throw EleccionNoEncontrada(id)
}
