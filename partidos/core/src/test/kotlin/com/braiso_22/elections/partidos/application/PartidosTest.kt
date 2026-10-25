package com.braiso_22.elections.partidos.application

import com.braiso_22.elections.kernel.DatoNoValido
import com.braiso_22.elections.kernel.PartidoId
import com.braiso_22.elections.partidos.domain.Partido
import com.braiso_22.elections.partidos.domain.PartidoNoEncontrado
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class PartidosTest {

    private val repositorio = RepositorioPartidosFalso()
    private val registro = RegistroDePartidosFalso(
        listOf(
            inscrito("1", "Partido de la Gente", "PG"),
            inscrito("2", "Partido Futuro", "PF"),
            inscrito("3", "Alianza Test", "AT"),
        )
    )
    private val importar = ImportarPartidosDelRegistro(registro, repositorio)
    private val listar = ListarPartidos(repositorio)

    @Test
    fun importarCreaLosPartidosDelRegistro() = runTest {
        assertEquals(ImportarPartidosDelRegistro.Resultado(nuevos = 3, actualizados = 0), importar())
        assertEquals(listOf("Alianza Test", "Partido Futuro", "Partido de la Gente"), listar().map { it.nombre })
    }

    @Test
    fun reimportarEsIdempotenteYActualizaLosCambios() = runTest {
        importar()
        val idAntes = listar("PF").single().id
        registro.inscritos = listOf(
            inscrito("1", "Partido de la Gente", "PG"),
            inscrito("2", "Partido del Futuro", "PDF"),
        )

        assertEquals(ImportarPartidosDelRegistro.Resultado(nuevos = 0, actualizados = 1), importar())
        val actualizado = listar("Futuro").single()
        assertEquals(idAntes, actualizado.id)
        assertEquals("PDF", actualizado.siglas.valor)
    }

    @Test
    fun buscarFiltraPorNombreOSiglasSinDistinguirMayusculas() = runTest {
        importar()
        assertEquals(listOf("PF", "PG"), listar("partido").map { it.siglas.valor })
        assertEquals(listOf("AT"), listar("at").map { it.siglas.valor })
        assertEquals(3, listar("  ").size)
    }

    @Test
    fun buscarPorSiglasIgnoraMayusculasYEspacios() = runTest {
        importar()
        assertEquals("Partido Futuro", BuscarPartidoPorSiglas(repositorio)(" pf ")?.nombre)
        assertNull(BuscarPartidoPorSiglas(repositorio)("XX"))
    }

    @Test
    fun obtenerUnPartidoQueNoExisteFalla() = runTest {
        assertFailsWith<PartidoNoEncontrado> { ObtenerPartido(repositorio)(PartidoId("no-existe")) }
    }

    @Test
    fun noSePuedeInscribirUnPartidoSinNombre() {
        assertFailsWith<DatoNoValido> { Partido.inscribir(inscrito("9", " ", "X")) }
    }

    @Test
    fun lasSiglasNoPuedenEstarVacias() {
        assertFailsWith<DatoNoValido> { inscrito("9", "Nombre", "") }
    }
}
