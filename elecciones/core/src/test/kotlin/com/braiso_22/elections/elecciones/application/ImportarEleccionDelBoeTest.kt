package com.braiso_22.elections.elecciones.application

import com.braiso_22.elections.elecciones.ANTES
import com.braiso_22.elections.elecciones.INICIO
import com.braiso_22.elections.elecciones.PERIODO
import com.braiso_22.elections.elecciones.RelojFijo
import com.braiso_22.elections.elecciones.domain.BoletinOficial
import com.braiso_22.elections.elecciones.domain.CandidaturaPublicada
import com.braiso_22.elections.elecciones.domain.CatalogoDePartidos
import com.braiso_22.elections.elecciones.domain.ConvocatoriaCongelada
import com.braiso_22.elections.elecciones.domain.ConvocatoriaNoPublicada
import com.braiso_22.elections.elecciones.domain.ConvocatoriaPublicada
import com.braiso_22.elections.elecciones.domain.Eleccion
import com.braiso_22.elections.elecciones.domain.EleccionNoEncontrada
import com.braiso_22.elections.elecciones.domain.EstadoEleccion
import com.braiso_22.elections.elecciones.domain.PartidosNoRegistrados
import com.braiso_22.elections.elecciones.domain.ReferenciaBoe
import com.braiso_22.elections.elecciones.domain.RepositorioElecciones
import com.braiso_22.elections.kernel.EleccionId
import com.braiso_22.elections.kernel.PartidoId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImportarEleccionDelBoeTest {

    private val referencia = ReferenciaBoe("BOE-A-2027-1")
    private val boletin = BoletinFalso(
        ConvocatoriaPublicada(
            referencia,
            "Elecciones Generales 2027",
            listOf(CandidaturaPublicada("PT", "Partido Test"), CandidaturaPublicada("pf", "Futuro Ya")),
        )
    )
    private val catalogo = CatalogoFalso(mapOf("PT" to PartidoId("pt"), "PF" to PartidoId("pf")))
    private val repositorio = RepositorioEleccionesFalso()
    private val reloj = RelojFijo(ANTES)
    private val importar = ImportarEleccionDelBoe(boletin, catalogo, repositorio, reloj)

    @Test
    fun creaLaEleccionEnlazandoLasCandidaturasConElCatalogo() = runTest {
        val resultado = importar(referencia, PERIODO)

        assertTrue(resultado.creada)
        assertEquals("Elecciones Generales 2027", resultado.eleccion.nombre)
        assertEquals(EstadoEleccion.CONVOCADA, resultado.eleccion.estado)
        assertEquals(
            listOf(PartidoId("pt") to "Partido Test", PartidoId("pf") to "Futuro Ya"),
            resultado.eleccion.candidaturas.map { it.partidoId to it.denominacion },
        )
        assertEquals(resultado.eleccion, ObtenerEleccion(repositorio, reloj)(resultado.eleccion.id))
    }

    @Test
    fun reimportarActualizaLaMismaEleccion() = runTest {
        val primera = importar(referencia, PERIODO)
        boletin.convocatoria = boletin.convocatoria.copy(nombre = "Generales 2027 (corrección)")

        val segunda = importar(referencia, PERIODO)

        assertFalse(segunda.creada)
        assertEquals(primera.eleccion.id, segunda.eleccion.id)
        assertEquals("Generales 2027 (corrección)", segunda.eleccion.nombre)
        assertEquals(1, ListarElecciones(repositorio, reloj)().size)
    }

    @Test
    fun noSePuedeReimportarConLaVotacionAbierta() = runTest {
        importar(referencia, PERIODO)
        reloj.ahora = INICIO

        assertFailsWith<ConvocatoriaCongelada> { importar(referencia, PERIODO) }
    }

    @Test
    fun fallaSiAlgunPartidoNoEstaEnElCatalogo() = runTest {
        boletin.convocatoria = boletin.convocatoria.copy(
            candidaturas = boletin.convocatoria.candidaturas + CandidaturaPublicada("XX", "Desconocido")
        )

        val error = assertFailsWith<PartidosNoRegistrados> { importar(referencia, PERIODO) }
        assertTrue(error.message!!.contains("XX"))
        assertTrue(repositorio.todas().isEmpty())
    }

    @Test
    fun fallaSiElBoeNoTieneEsaConvocatoria() = runTest {
        assertFailsWith<ConvocatoriaNoPublicada> { importar(ReferenciaBoe("BOE-A-OTRA"), PERIODO) }
    }

    @Test
    fun obtenerUnaEleccionQueNoExisteFalla() = runTest {
        assertFailsWith<EleccionNoEncontrada> { ObtenerEleccion(repositorio, reloj)(EleccionId("nope")) }
    }

    private class BoletinFalso(var convocatoria: ConvocatoriaPublicada) : BoletinOficial {
        override suspend fun convocatoria(referencia: ReferenciaBoe) =
            convocatoria.takeIf { it.referencia == referencia }
    }

    private class CatalogoFalso(private val porSiglas: Map<String, PartidoId>) : CatalogoDePartidos {
        override suspend fun partidoConSiglas(siglas: String) = porSiglas[siglas.uppercase()]
    }

    private class RepositorioEleccionesFalso : RepositorioElecciones {
        private val elecciones = linkedMapOf<EleccionId, Eleccion>()
        override suspend fun todas() = elecciones.values.toList()
        override suspend fun porId(id: EleccionId) = elecciones[id]
        override suspend fun porReferencia(referencia: ReferenciaBoe) =
            elecciones.values.firstOrNull { it.referenciaBoe == referencia }

        override suspend fun guardar(eleccion: Eleccion) {
            elecciones[eleccion.id] = eleccion
        }
    }
}
