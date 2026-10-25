package com.braiso_22.elections.partidos.domain

import com.braiso_22.elections.kernel.PartidoId

interface RepositorioPartidos {
    suspend fun todos(): List<Partido>
    suspend fun porId(id: PartidoId): Partido?
    suspend fun porNumeroDeRegistro(numero: NumeroDeRegistro): Partido?
    suspend fun guardar(partido: Partido)
}

/** Fuente oficial del catálogo: el Registro de Partidos Políticos del Ministerio del Interior. */
interface RegistroDePartidos {
    suspend fun partidosInscritos(): List<PartidoInscrito>
}

data class PartidoInscrito(
    val numeroDeRegistro: NumeroDeRegistro,
    val nombre: String,
    val siglas: Siglas,
)
