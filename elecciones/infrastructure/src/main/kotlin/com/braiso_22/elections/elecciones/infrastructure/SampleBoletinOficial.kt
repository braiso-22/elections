package com.braiso_22.elections.elecciones.infrastructure

import com.braiso_22.elections.elecciones.domain.BoletinOficial
import com.braiso_22.elections.elecciones.domain.CandidaturaPublicada
import com.braiso_22.elections.elecciones.domain.ConvocatoriaPublicada
import com.braiso_22.elections.elecciones.domain.ReferenciaBoe

/** Sustituto del BOE hasta que [boe.BoeApiClient] implemente el puerto. */
class SampleBoletinOficial : BoletinOficial {
    private val convocatorias = listOf(
        ConvocatoriaPublicada(
            referencia = ReferenciaBoe(REFERENCIA_EJEMPLO),
            nombre = "Elecciones de ejemplo",
            candidaturas = listOf(
                CandidaturaPublicada(siglas = "PT", denominacion = "Partido Test"),
                CandidaturaPublicada(siglas = "PE", denominacion = "Partido Ejemplo"),
                CandidaturaPublicada(siglas = "PG", denominacion = "Partido de la Gente"),
            ),
        ),
    ).associateBy { it.referencia }

    override suspend fun convocatoria(referencia: ReferenciaBoe): ConvocatoriaPublicada? = convocatorias[referencia]

    companion object {
        const val REFERENCIA_EJEMPLO = "BOE-A-EJEMPLO-1"
    }
}
