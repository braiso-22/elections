package com.braiso_22.elections.votacion.application

import com.braiso_22.elections.kernel.BusDeEventos
import com.braiso_22.elections.kernel.CandidaturaId
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.EventoDeDominio
import com.braiso_22.elections.kernel.VotanteId
import com.braiso_22.elections.votacion.domain.CandidaturaNoPresentada
import com.braiso_22.elections.votacion.domain.ConsultaElecciones
import com.braiso_22.elections.votacion.domain.EleccionDesconocida
import com.braiso_22.elections.votacion.domain.EleccionNoAbierta
import com.braiso_22.elections.votacion.domain.Opcion
import com.braiso_22.elections.votacion.domain.RepositorioVotos
import com.braiso_22.elections.votacion.domain.SituacionEleccion
import com.braiso_22.elections.votacion.domain.Voto
import com.braiso_22.elections.votacion.domain.VotoCambiado
import com.braiso_22.elections.votacion.domain.VotoEmitido
import com.braiso_22.elections.votacion.domain.VotoNoEncontrado
import kotlinx.coroutines.test.runTest
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class VotarTest {

    private val eleccion = EleccionId("generales")
    private val ana = VotanteId("ana")
    private val pt = Opcion.ACandidatura(CandidaturaId("pt"))
    private val pf = Opcion.ACandidatura(CandidaturaId("pf"))

    private val elecciones = ConsultaEleccionesFalsa(
        eleccion to SituacionEleccion(admiteVotos = true, candidaturas = setOf(CandidaturaId("pt"), CandidaturaId("pf")))
    )
    private val votos = RepositorioVotosFalso()
    private val bus = BusQueGuarda()
    private val reloj = object : Clock {
        var ahora = Instant.parse("2027-05-23T10:00:00Z")
        override fun now() = ahora
    }
    private val votar = Votar(elecciones, votos, bus, reloj)

    @Test
    fun elPrimerVotoSeEmite() = runTest {
        val voto = votar(eleccion, ana, pt)

        assertEquals(pt, voto.opcion)
        assertEquals(reloj.ahora, voto.emitidoEn)
        assertEquals(listOf<EventoDeDominio>(VotoEmitido(eleccion, ana, pt)), bus.publicados)
        assertEquals(pt, ConsultarMiVoto(votos)(eleccion, ana).opcion)
    }

    @Test
    fun volverAVotarCambiaElVoto() = runTest {
        votar(eleccion, ana, pt)
        reloj.ahora += 5.minutes

        val voto = votar(eleccion, ana, Opcion.ContraElSistema)

        assertEquals(Opcion.ContraElSistema, voto.opcion)
        assertEquals(reloj.ahora, voto.emitidoEn)
        assertEquals(VotoCambiado(eleccion, ana, pt, Opcion.ContraElSistema), bus.publicados.last())
    }

    @Test
    fun votarLoMismoNoGeneraEventos() = runTest {
        votar(eleccion, ana, pt)
        votar(eleccion, ana, pt)

        assertEquals(1, bus.publicados.size)
    }

    @Test
    fun noSePuedeVotarSiLaEleccionNoEstaAbierta() = runTest {
        elecciones.situaciones[eleccion] = SituacionEleccion(admiteVotos = false, candidaturas = emptySet())

        assertFailsWith<EleccionNoAbierta> { votar(eleccion, ana, Opcion.EnBlanco) }
        assertTrue(bus.publicados.isEmpty())
    }

    @Test
    fun noSePuedeVotarAUnaCandidaturaQueNoSePresenta() = runTest {
        assertFailsWith<CandidaturaNoPresentada> {
            votar(eleccion, ana, Opcion.ACandidatura(CandidaturaId("otra")))
        }
    }

    @Test
    fun noSePuedeVotarEnUnaEleccionQueNoExiste() = runTest {
        assertFailsWith<EleccionDesconocida> { votar(EleccionId("no-existe"), ana, pf) }
    }

    @Test
    fun consultarUnVotoQueNoExisteFalla() = runTest {
        assertFailsWith<VotoNoEncontrado> { ConsultarMiVoto(votos)(eleccion, ana) }
    }

    private class ConsultaEleccionesFalsa(vararg situaciones: Pair<EleccionId, SituacionEleccion>) : ConsultaElecciones {
        val situaciones = mutableMapOf(*situaciones)
        override suspend fun situacion(eleccionId: EleccionId) = situaciones[eleccionId]
    }

    private class RepositorioVotosFalso : RepositorioVotos {
        private val votos = mutableMapOf<Pair<EleccionId, VotanteId>, Voto>()
        override suspend fun de(eleccionId: EleccionId, votanteId: VotanteId) = votos[eleccionId to votanteId]
        override suspend fun guardar(voto: Voto) {
            votos[voto.eleccionId to voto.votanteId] = voto
        }
    }

    private class BusQueGuarda : BusDeEventos {
        val publicados = mutableListOf<EventoDeDominio>()
        override suspend fun publicar(eventos: List<EventoDeDominio>) {
            publicados += eventos
        }

        override fun <E : EventoDeDominio> suscribir(tipo: KClass<E>, manejador: suspend (E) -> Unit) = Unit
    }
}
