package com.braiso_22.elections.elecciones.application

import com.braiso_22.elections.elecciones.domain.BoletinOficial
import com.braiso_22.elections.elecciones.domain.CandidaturaPublicada
import com.braiso_22.elections.elecciones.domain.CatalogoDePartidos
import com.braiso_22.elections.elecciones.domain.ConvocatoriaNoPublicada
import com.braiso_22.elections.elecciones.domain.Eleccion
import com.braiso_22.elections.elecciones.domain.NuevaCandidatura
import com.braiso_22.elections.elecciones.domain.PartidosNoRegistrados
import com.braiso_22.elections.elecciones.domain.PeriodoVotacion
import com.braiso_22.elections.elecciones.domain.ReferenciaBoe
import com.braiso_22.elections.elecciones.domain.RepositorioElecciones
import kotlin.time.Clock

/**
 * Crea la elección a partir de su convocatoria en el BOE o, si ya existía, la actualiza con lo
 * publicado. El periodo de votación lo decide el administrador: no tiene por qué coincidir con
 * el día de la elección real.
 */
class ImportarEleccionDelBoe(
    private val boletin: BoletinOficial,
    private val catalogo: CatalogoDePartidos,
    private val elecciones: RepositorioElecciones,
    private val reloj: Clock,
) {
    data class Resultado(val eleccion: VistaEleccion, val creada: Boolean)

    suspend operator fun invoke(referencia: ReferenciaBoe, periodo: PeriodoVotacion): Resultado {
        val convocatoria = boletin.convocatoria(referencia) ?: throw ConvocatoriaNoPublicada(referencia)
        val candidaturas = enlazarConCatalogo(convocatoria.candidaturas)
        val ahora = reloj.now()

        val existente = elecciones.porReferencia(referencia)
        val eleccion = existente
            ?.apply { actualizarConvocatoria(convocatoria.nombre, periodo, candidaturas, ahora) }
            ?: Eleccion.convocar(referencia, convocatoria.nombre, periodo, candidaturas, ahora)
        elecciones.guardar(eleccion)
        return Resultado(eleccion.vistaEn(ahora), creada = existente == null)
    }

    private suspend fun enlazarConCatalogo(publicadas: List<CandidaturaPublicada>): List<NuevaCandidatura> {
        val sinPartido = mutableListOf<String>()
        val enlazadas = publicadas.mapNotNull { publicada ->
            val partido = catalogo.partidoConSiglas(publicada.siglas)
            if (partido == null) sinPartido += publicada.siglas
            partido?.let { NuevaCandidatura(it, publicada.denominacion) }
        }
        if (sinPartido.isNotEmpty()) throw PartidosNoRegistrados(sinPartido)
        return enlazadas
    }
}
