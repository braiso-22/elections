package com.braiso_22.elections.partidos.infrastructure

import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.partidos.domain.NumeroDeRegistro
import com.braiso_22.elections.partidos.domain.Partido
import com.braiso_22.elections.partidos.domain.RepositorioPartidos
import java.util.concurrent.ConcurrentHashMap

class InMemoryPartidoRepository : RepositorioPartidos {
    private val partidos = ConcurrentHashMap<PartidoId, Partido>()

    override suspend fun todos(): List<Partido> = partidos.values.toList()

    override suspend fun porId(id: PartidoId): Partido? = partidos[id]

    override suspend fun porNumeroDeRegistro(numero: NumeroDeRegistro): Partido? =
        partidos.values.firstOrNull { it.numeroDeRegistro == numero }

    override suspend fun guardar(partido: Partido) {
        partidos[partido.id] = partido
    }
}
