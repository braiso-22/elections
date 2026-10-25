package com.braiso_22.elections.resultados.infrastructure

import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.resultados.domain.Recuento
import com.braiso_22.elections.resultados.domain.RepositorioRecuentos
import java.util.concurrent.ConcurrentHashMap

class InMemoryRecuentoRepository : RepositorioRecuentos {
    private val recuentos = ConcurrentHashMap<EleccionId, Recuento>()

    override suspend fun de(eleccionId: EleccionId): Recuento? = recuentos[eleccionId]

    override suspend fun actualizar(eleccionId: EleccionId, cambio: (Recuento) -> Recuento) {
        recuentos.compute(eleccionId) { _, actual -> cambio(actual ?: Recuento(eleccionId)) }
    }
}
