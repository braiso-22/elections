package com.braiso_22.elections.partidos.infrastructure

import com.braiso_22.elections.partidos.domain.NumeroDeRegistro
import com.braiso_22.elections.partidos.domain.PartidoInscrito
import com.braiso_22.elections.partidos.domain.RegistroDePartidos
import com.braiso_22.elections.partidos.domain.Siglas

/** Sustituto del Registro de Partidos de Interior hasta tener la integración real. */
class SampleRegistroDePartidos : RegistroDePartidos {
    override suspend fun partidosInscritos(): List<PartidoInscrito> = listOf(
        inscrito("0001", "Partido Test", "PT"),
        inscrito("0002", "Partido Ejemplo", "PE"),
        inscrito("0003", "Partido de la Gente", "PG"),
        inscrito("0004", "Partido Innovador", "PI"),
        inscrito("0005", "Partido Futuro", "PF"),
    )

    private fun inscrito(numero: String, nombre: String, siglas: String) =
        PartidoInscrito(NumeroDeRegistro(numero), nombre, Siglas(siglas))
}
