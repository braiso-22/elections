package com.braiso_22.elections.partidos.application

import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.partidos.domain.NumeroDeRegistro
import com.braiso_22.elections.partidos.domain.Partido
import com.braiso_22.elections.partidos.domain.PartidoInscrito
import com.braiso_22.elections.partidos.domain.RegistroDePartidos
import com.braiso_22.elections.partidos.domain.RepositorioPartidos
import com.braiso_22.elections.partidos.domain.Siglas

class RepositorioPartidosFalso : RepositorioPartidos {
    private val partidos = linkedMapOf<PartidoId, Partido>()

    override suspend fun todos() = partidos.values.toList()
    override suspend fun porId(id: PartidoId) = partidos[id]
    override suspend fun porNumeroDeRegistro(numero: NumeroDeRegistro) =
        partidos.values.firstOrNull { it.numeroDeRegistro == numero }

    override suspend fun guardar(partido: Partido) {
        partidos[partido.id] = partido
    }
}

class RegistroDePartidosFalso(var inscritos: List<PartidoInscrito>) : RegistroDePartidos {
    override suspend fun partidosInscritos() = inscritos
}

fun inscrito(numero: String, nombre: String, siglas: String) =
    PartidoInscrito(NumeroDeRegistro(numero), nombre, Siglas(siglas))
