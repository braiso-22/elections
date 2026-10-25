package com.braiso_22.elections.elecciones.domain

import com.braiso_22.elections.elecciones.ANTES
import com.braiso_22.elections.elecciones.FIN
import com.braiso_22.elections.elecciones.INICIO
import com.braiso_22.elections.elecciones.PERIODO
import com.braiso_22.elections.kernel.DatoNoValido
import com.braiso_22.elections.kernel.PartidoId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class EleccionTest {

    private val pt = NuevaCandidatura(PartidoId("pt"), "Partido Test")
    private val pf = NuevaCandidatura(PartidoId("pf"), "Partido Futuro")

    private fun convocada(vararg candidaturas: NuevaCandidatura = arrayOf(pt, pf)) =
        Eleccion.convocar(ReferenciaBoe("BOE-A-1"), "Generales", PERIODO, candidaturas.toList(), ANTES)

    @Test
    fun elEstadoDependeDelMomentoRespectoAlPeriodo() {
        val eleccion = convocada()
        assertEquals(EstadoEleccion.CONVOCADA, eleccion.estadoEn(INICIO - 1.seconds))
        assertEquals(EstadoEleccion.ABIERTA, eleccion.estadoEn(INICIO))
        assertEquals(EstadoEleccion.ABIERTA, eleccion.estadoEn(FIN - 1.seconds))
        assertEquals(EstadoEleccion.CERRADA, eleccion.estadoEn(FIN))
        assertTrue(eleccion.admiteVotosEn(INICIO))
        assertFalse(eleccion.admiteVotosEn(FIN))
    }

    @Test
    fun antesDeAbrirSePuedenCambiarLasCandidaturasYConservanSuId() {
        val eleccion = convocada()
        val idPt = eleccion.candidaturas.first { it.partidoId == pt.partidoId }.id
        val pg = NuevaCandidatura(PartidoId("pg"), "Partido de la Gente")

        eleccion.actualizarConvocatoria("Generales 2027", PERIODO, listOf(pt, pg), ANTES)

        assertEquals("Generales 2027", eleccion.nombre)
        assertEquals(listOf(pt.partidoId, pg.partidoId), eleccion.candidaturas.map { it.partidoId })
        assertEquals(idPt, eleccion.candidaturas.first { it.partidoId == pt.partidoId }.id)
        assertNotEquals(idPt, eleccion.candidaturas.first { it.partidoId == pg.partidoId }.id)
    }

    @Test
    fun alAbrirseLaVotacionLaConvocatoriaQuedaCongelada() {
        val eleccion = convocada()
        assertFailsWith<ConvocatoriaCongelada> {
            eleccion.actualizarConvocatoria("Otra", PERIODO, listOf(pt), INICIO)
        }
        assertEquals(2, eleccion.candidaturas.size)
    }

    @Test
    fun unPartidoNoPuedePresentarseDosVeces() {
        assertFailsWith<DatoNoValido> { convocada(pt, pt.copy(denominacion = "Otra marca")) }
    }

    @Test
    fun hacenFaltaCandidaturas() {
        assertFailsWith<DatoNoValido> { convocada(*emptyArray()) }
    }

    @Test
    fun noSePuedeConvocarConElPeriodoTerminado() {
        assertFailsWith<DatoNoValido> {
            Eleccion.convocar(ReferenciaBoe("BOE-A-1"), "Generales", PERIODO, listOf(pt), FIN)
        }
    }

    @Test
    fun elPeriodoDebeEmpezarAntesDeTerminar() {
        assertFailsWith<DatoNoValido> { PeriodoVotacion(FIN, INICIO) }
    }
}
