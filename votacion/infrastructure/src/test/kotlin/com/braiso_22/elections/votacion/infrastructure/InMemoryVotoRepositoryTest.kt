package com.braiso_22.elections.votacion.infrastructure

import com.braiso_22.elections.kernel.ConflictoDeConcurrencia
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.VotanteId
import com.braiso_22.elections.votacion.domain.Opcion
import com.braiso_22.elections.votacion.domain.Voto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class InMemoryVotoRepositoryTest {

    private val repositorio = InMemoryVotoRepository()
    private val eleccion = EleccionId("e")
    private val ana = VotanteId("ana")
    private val ahora = Instant.parse("2027-05-23T10:00:00Z")

    @Test
    fun loLeidoNoCambiaHastaGuardar() = runTest {
        repositorio.guardar(Voto.emitir(eleccion, ana, Opcion.EnBlanco, ahora))

        repositorio.de(eleccion, ana)!!.cambiar(Opcion.ContraElSistema, ahora)

        assertEquals(Opcion.EnBlanco, repositorio.de(eleccion, ana)!!.opcion)
    }

    @Test
    fun dosPrimerosVotosSimultaneosDelMismoVotanteChocan() = runTest {
        val primero = Voto.emitir(eleccion, ana, Opcion.EnBlanco, ahora)
        val segundo = Voto.emitir(eleccion, ana, Opcion.ContraElSistema, ahora)

        repositorio.guardar(primero)

        assertFailsWith<ConflictoDeConcurrencia> { repositorio.guardar(segundo) }
    }

    @Test
    fun cambiarUnVotoLeidoAntesDeOtroCambioChoca() = runTest {
        repositorio.guardar(Voto.emitir(eleccion, ana, Opcion.EnBlanco, ahora))
        val enUnaPeticion = repositorio.de(eleccion, ana)!!
        val enOtra = repositorio.de(eleccion, ana)!!

        enUnaPeticion.cambiar(Opcion.ContraElSistema, ahora)
        repositorio.guardar(enUnaPeticion)
        enOtra.cambiar(Opcion.ContraElSistema, ahora)

        assertFailsWith<ConflictoDeConcurrencia> { repositorio.guardar(enOtra) }
    }
}
