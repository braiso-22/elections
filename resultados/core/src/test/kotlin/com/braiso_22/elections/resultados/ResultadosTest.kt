package com.braiso_22.elections.resultados

import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.resultados.application.Cifra
import com.braiso_22.elections.resultados.application.ConsultarResultados
import com.braiso_22.elections.resultados.application.ContabilizarVotos
import com.braiso_22.elections.resultados.domain.CandidaturaConocida
import com.braiso_22.elections.resultados.domain.ConsultaCandidaturas
import com.braiso_22.elections.resultados.domain.EleccionSinResultados
import com.braiso_22.elections.resultados.domain.OpcionContada
import com.braiso_22.elections.resultados.domain.Recuento
import com.braiso_22.elections.resultados.domain.RepositorioRecuentos
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResultadosTest {

    private val eleccion = EleccionId("e")
    private val pt = CandidaturaId("pt")
    private val pf = CandidaturaId("pf")
    private val recuentos = RepositorioRecuentosFalso()
    private val contabilizar = ContabilizarVotos(recuentos)
    private val consultar = ConsultarResultados(
        recuentos,
        object : ConsultaCandidaturas {
            override suspend fun candidaturasDe(eleccionId: EleccionId) =
                if (eleccionId == eleccion) listOf(CandidaturaConocida(pt, "Partido Test"), CandidaturaConocida(pf, "Partido Futuro"))
                else null
        },
    )

    @Test
    fun sinVotosTodoEstaACero() = runTest {
        val resultados = consultar(eleccion)

        assertEquals(0, resultados.participacion)
        assertEquals(Cifra(0, 0.0), resultados.enBlanco)
        assertEquals(listOf(Cifra(0, 0.0), Cifra(0, 0.0)), resultados.candidaturas.map { it.cifra })
    }

    @Test
    fun cuentaVotosYPorcentajesYOrdenaPorVotos() = runTest {
        contabilizar.votoEmitido(eleccion, OpcionContada.Candidatura(pf))
        contabilizar.votoEmitido(eleccion, OpcionContada.Candidatura(pf))
        contabilizar.votoEmitido(eleccion, OpcionContada.EnBlanco)
        contabilizar.votoEmitido(eleccion, OpcionContada.ContraElSistema)

        val resultados = consultar(eleccion)

        assertEquals(4, resultados.participacion)
        assertEquals(Cifra(1, 25.0), resultados.enBlanco)
        assertEquals(Cifra(1, 25.0), resultados.contraElSistema)
        assertEquals(
            listOf("Partido Futuro" to Cifra(2, 50.0), "Partido Test" to Cifra(0, 0.0)),
            resultados.candidaturas.map { it.denominacion to it.cifra },
        )
    }

    @Test
    fun cambiarUnVotoLoMueveSinCambiarLaParticipacion() = runTest {
        contabilizar.votoEmitido(eleccion, OpcionContada.Candidatura(pt))
        contabilizar.votoCambiado(eleccion, OpcionContada.Candidatura(pt), OpcionContada.EnBlanco)

        val resultados = consultar(eleccion)

        assertEquals(1, resultados.participacion)
        assertEquals(Cifra(1, 100.0), resultados.enBlanco)
        assertEquals(0, resultados.candidaturas.first { it.candidaturaId == pt }.cifra.votos)
    }

    @Test
    fun noHayResultadosDeUnaEleccionQueNoExiste() = runTest {
        assertFailsWith<EleccionSinResultados> { consultar(EleccionId("otra")) }
    }

    private class RepositorioRecuentosFalso : RepositorioRecuentos {
        private val recuentos = mutableMapOf<EleccionId, Recuento>()
        override suspend fun de(eleccionId: EleccionId) = recuentos[eleccionId]
        override suspend fun actualizar(eleccionId: EleccionId, cambio: (Recuento) -> Recuento) {
            recuentos[eleccionId] = cambio(recuentos[eleccionId] ?: Recuento(eleccionId))
        }
    }
}
