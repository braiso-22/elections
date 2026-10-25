package com.braiso_22.elections.elecciones.domain

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.kernel.validar
import kotlin.time.Instant

/**
 * Elección convocada en el BOE. Se vota mientras dura su [periodo] y sus candidaturas
 * quedan congeladas en cuanto se abre la votación.
 */
class Eleccion private constructor(
    val id: EleccionId,
    val referenciaBoe: ReferenciaBoe,
    nombre: String,
    periodo: PeriodoVotacion,
    candidaturas: List<Candidatura>,
) {
    var nombre: String = nombre
        private set
    var periodo: PeriodoVotacion = periodo
        private set
    var candidaturas: List<Candidatura> = candidaturas
        private set

    init {
        validarConvocatoria(nombre, candidaturas)
    }

    fun estadoEn(ahora: Instant): EstadoEleccion = periodo.estadoEn(ahora)

    fun admiteVotosEn(ahora: Instant): Boolean = estadoEn(ahora) == EstadoEleccion.ABIERTA

    /**
     * Aplica una nueva publicación de la convocatoria. Las candidaturas de partidos que ya se
     * presentaban conservan su id.
     */
    fun actualizarConvocatoria(
        nombre: String,
        periodo: PeriodoVotacion,
        candidaturas: List<NuevaCandidatura>,
        ahora: Instant,
    ) {
        if (estadoEn(ahora) != EstadoEleccion.CONVOCADA) throw ConvocatoriaCongelada(id)
        val actualizadas = candidaturas.map { nueva ->
            val existente = this.candidaturas.firstOrNull { it.partidoId == nueva.partidoId }
            Candidatura(existente?.id ?: CandidaturaId.nuevo(), nueva.partidoId, nueva.denominacion)
        }
        validarConvocatoria(nombre, actualizadas)
        this.nombre = nombre
        this.periodo = periodo
        this.candidaturas = actualizadas
    }

    companion object {
        fun convocar(
            referenciaBoe: ReferenciaBoe,
            nombre: String,
            periodo: PeriodoVotacion,
            candidaturas: List<NuevaCandidatura>,
            ahora: Instant,
        ): Eleccion {
            validar(periodo.fin > ahora) { "El periodo de votación ya ha terminado" }
            return Eleccion(
                id = EleccionId.nuevo(),
                referenciaBoe = referenciaBoe,
                nombre = nombre,
                periodo = periodo,
                candidaturas = candidaturas.map { Candidatura(CandidaturaId.nuevo(), it.partidoId, it.denominacion) },
            )
        }

        /** Para los repositorios: reconstruye una elección ya guardada. */
        fun reconstruir(
            id: EleccionId,
            referenciaBoe: ReferenciaBoe,
            nombre: String,
            periodo: PeriodoVotacion,
            candidaturas: List<Candidatura>,
        ) = Eleccion(id, referenciaBoe, nombre, periodo, candidaturas)
    }
}

private fun validarConvocatoria(nombre: String, candidaturas: List<Candidatura>) {
    validar(nombre.isNotBlank()) { "El nombre de la elección no puede estar vacío" }
    validar(candidaturas.isNotEmpty()) { "Una elección necesita al menos una candidatura" }
    validar(candidaturas.distinctBy { it.partidoId }.size == candidaturas.size) {
        "Un partido no puede presentar dos candidaturas a la misma elección"
    }
}

/** Un partido que se presenta a una elección concreta. */
data class Candidatura(
    val id: CandidaturaId,
    val partidoId: PartidoId,
    /** Nombre con el que concurre, que puede no coincidir con el del partido (coaliciones, marcas locales…). */
    val denominacion: String,
) {
    init {
        validar(denominacion.isNotBlank()) { "La denominación de la candidatura no puede estar vacía" }
    }
}

data class NuevaCandidatura(val partidoId: PartidoId, val denominacion: String)

data class PeriodoVotacion(val inicio: Instant, val fin: Instant) {
    init {
        validar(inicio < fin) { "El periodo de votación debe empezar antes de terminar" }
    }

    fun estadoEn(ahora: Instant): EstadoEleccion = when {
        ahora < inicio -> EstadoEleccion.CONVOCADA
        ahora < fin -> EstadoEleccion.ABIERTA
        else -> EstadoEleccion.CERRADA
    }
}

enum class EstadoEleccion { CONVOCADA, ABIERTA, CERRADA }

/** Identificador de la disposición del BOE que convoca la elección, p. ej. "BOE-A-2023-12345". */
@JvmInline
value class ReferenciaBoe(val valor: String) {
    init {
        validar(valor.isNotBlank()) { "La referencia del BOE no puede estar vacía" }
    }
}
