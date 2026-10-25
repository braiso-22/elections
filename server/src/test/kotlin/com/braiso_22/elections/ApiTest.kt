package com.braiso_22.elections

import com.braiso_22.elections.kernel.VotanteId
import com.braiso_22.elections.shared.CifraDto
import com.braiso_22.elections.shared.EleccionDto
import com.braiso_22.elections.shared.EleccionResumenDto
import com.braiso_22.elections.shared.ErrorDto
import com.braiso_22.elections.shared.EstadoEleccionDto
import com.braiso_22.elections.shared.ImportacionPartidosDto
import com.braiso_22.elections.shared.ImportarEleccionRequest
import com.braiso_22.elections.shared.OpcionDto
import com.braiso_22.elections.shared.PartidoDto
import com.braiso_22.elections.shared.ResultadosDto
import com.braiso_22.elections.shared.VotoDto
import com.braiso_22.elections.shared.VotoRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class ApiTest {

    private val inicio = Instant.parse("2027-05-23T07:00:00Z")
    private val fin = inicio + 1.days
    private val reloj = object : Clock {
        var ahora = inicio - 7.days
        override fun now() = ahora
    }

    @Test
    fun health() = api {
        assertEquals(HttpStatusCode.OK, client.get("/health").status)
    }

    @Test
    fun flujoCompletoDeUnaEleccion() = api {
        val importacion = client.post("/v1/admin/importaciones/registro-partidos") { bearerAuth(ADMIN) }
        assertEquals(ImportacionPartidosDto(nuevos = 5, actualizados = 0), importacion.body())

        val respuesta = importarEleccion()
        assertEquals(HttpStatusCode.Created, respuesta.status)
        val eleccion = respuesta.body<EleccionDto>()
        assertEquals(EstadoEleccionDto.CONVOCADA, eleccion.estado)
        assertEquals(listOf("Partido Test", "Partido Ejemplo", "Partido de la Gente"), eleccion.candidaturas.map { it.denominacion })
        assertEquals(
            listOf(EleccionResumenDto(eleccion.id, "Elecciones de ejemplo", EstadoEleccionDto.CONVOCADA, inicio.toString(), fin.toString())),
            client.get("/v1/elecciones").body<List<EleccionResumenDto>>(),
        )

        reloj.ahora = inicio + 1.hours
        val pt = eleccion.candidatura("Partido Test")
        val pe = eleccion.candidatura("Partido Ejemplo")
        assertEquals(HttpStatusCode.OK, votar(eleccion.id, "ana", VotoRequest(OpcionDto.CANDIDATURA, pt)).status)
        votar(eleccion.id, "bea", VotoRequest(OpcionDto.EN_BLANCO))
        votar(eleccion.id, "carlos", VotoRequest(OpcionDto.CONTRA_EL_SISTEMA))
        val cambio = votar(eleccion.id, "ana", VotoRequest(OpcionDto.CANDIDATURA, pe)).body<VotoDto>()
        assertEquals(VotoDto(eleccion.id, OpcionDto.CANDIDATURA, pe, reloj.ahora.toString()), cambio)

        assertEquals(cambio, client.get("/v1/elecciones/${eleccion.id}/voto") { bearerAuth("ana") }.body<VotoDto>())

        val resultados = client.get("/v1/elecciones/${eleccion.id}/resultados").body<ResultadosDto>()
        val tercio = 100.0 / 3
        assertEquals(3, resultados.participacion)
        assertEquals(CifraDto(1, tercio), resultados.enBlanco)
        assertEquals(CifraDto(1, tercio), resultados.contraElSistema)
        assertEquals(
            listOf("Partido Ejemplo" to CifraDto(1, tercio), "Partido Test" to CifraDto(0, 0.0), "Partido de la Gente" to CifraDto(0, 0.0)),
            resultados.candidaturas.map { it.denominacion to it.cifra },
        )
    }

    @Test
    fun buscarPartidos() = api {
        client.post("/v1/admin/importaciones/registro-partidos") { bearerAuth(ADMIN) }

        val partidos = client.get("/v1/partidos?busqueda=futuro").body<List<PartidoDto>>()

        assertEquals(listOf("PF"), partidos.map { it.siglas })
        assertEquals(partidos.single(), client.get("/v1/partidos/${partidos.single().id}").body<PartidoDto>())
    }

    @Test
    fun sinTokenNoSePuedeVotarNiAdministrar() = api {
        assertEquals(HttpStatusCode.Unauthorized, client.put("/v1/elecciones/x/voto").status)
        assertEquals(HttpStatusCode.Unauthorized, client.post("/v1/admin/importaciones/registro-partidos").status)
    }

    @Test
    fun unVotanteNormalNoPuedeAdministrar() = api {
        val respuesta = client.post("/v1/admin/importaciones/registro-partidos") { bearerAuth("ana") }

        assertEquals(HttpStatusCode.Forbidden, respuesta.status)
        assertEquals("NoEsAdministrador", respuesta.body<ErrorDto>().codigo)
    }

    @Test
    fun soloSeVotaDuranteElPeriodo() = api {
        val eleccion = prepararEleccion()

        assertError(HttpStatusCode.Conflict, "EleccionNoAbierta", votar(eleccion.id, "ana", VotoRequest(OpcionDto.EN_BLANCO)))
        reloj.ahora = fin
        assertError(HttpStatusCode.Conflict, "EleccionNoAbierta", votar(eleccion.id, "ana", VotoRequest(OpcionDto.EN_BLANCO)))
    }

    @Test
    fun laConvocatoriaNoSePuedeReimportarConLaVotacionAbierta() = api {
        prepararEleccion()
        reloj.ahora = inicio

        assertError(HttpStatusCode.Conflict, "ConvocatoriaCongelada", importarEleccion())
    }

    @Test
    fun votosNoValidos() = api {
        val eleccion = prepararEleccion()
        reloj.ahora = inicio

        assertError(
            HttpStatusCode.UnprocessableEntity, "CandidaturaNoPresentada",
            votar(eleccion.id, "ana", VotoRequest(OpcionDto.CANDIDATURA, "otra")),
        )
        assertError(HttpStatusCode.UnprocessableEntity, "DatoNoValido", votar(eleccion.id, "ana", VotoRequest(OpcionDto.CANDIDATURA)))
        assertError(
            HttpStatusCode.UnprocessableEntity, "DatoNoValido",
            votar(eleccion.id, "ana", VotoRequest(OpcionDto.EN_BLANCO, eleccion.candidaturas.first().id)),
        )
        val malFormado = client.put("/v1/elecciones/${eleccion.id}/voto") {
            bearerAuth("ana")
            contentType(ContentType.Application.Json)
            setBody("""{"opcion":"NO_EXISTE"}""")
        }
        assertError(HttpStatusCode.BadRequest, "PeticionMalFormada", malFormado)
    }

    @Test
    fun eleccionesQueNoExisten() = api {
        assertError(HttpStatusCode.NotFound, "EleccionNoEncontrada", client.get("/v1/elecciones/no-existe"))
        assertError(HttpStatusCode.NotFound, "EleccionSinResultados", client.get("/v1/elecciones/no-existe/resultados"))
        assertError(HttpStatusCode.NotFound, "EleccionDesconocida", votar("no-existe", "ana", VotoRequest(OpcionDto.EN_BLANCO)))
        assertError(HttpStatusCode.NotFound, "VotoNoEncontrado", client.get("/v1/elecciones/no-existe/voto") { bearerAuth("ana") })
    }

    @Test
    fun noSeImportaUnaEleccionConPartidosFueraDelCatalogo() = api {
        assertError(HttpStatusCode.UnprocessableEntity, "PartidosNoRegistrados", importarEleccion())
    }

    @Test
    fun fechasNoValidasAlImportar() = api {
        val respuesta = client.post("/v1/admin/importaciones/boe") {
            bearerAuth(ADMIN)
            contentType(ContentType.Application.Json)
            setBody(ImportarEleccionRequest("BOE-A-EJEMPLO-1", "mañana", fin.toString()))
        }
        assertError(HttpStatusCode.UnprocessableEntity, "DatoNoValido", respuesta)
    }

    private fun api(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            module(Configuracion(puerto = 0, administradores = setOf(VotanteId(ADMIN)), reloj = reloj))
        }
        client = createClient {
            install(ContentNegotiation) { json() }
        }
        block()
    }

    private suspend fun ApplicationTestBuilder.prepararEleccion(): EleccionDto {
        client.post("/v1/admin/importaciones/registro-partidos") { bearerAuth(ADMIN) }
        return importarEleccion().body()
    }

    private suspend fun ApplicationTestBuilder.importarEleccion(): HttpResponse =
        client.post("/v1/admin/importaciones/boe") {
            bearerAuth(ADMIN)
            contentType(ContentType.Application.Json)
            setBody(ImportarEleccionRequest("BOE-A-EJEMPLO-1", inicio.toString(), fin.toString()))
        }

    private suspend fun ApplicationTestBuilder.votar(eleccionId: String, votante: String, voto: VotoRequest): HttpResponse =
        client.put("/v1/elecciones/$eleccionId/voto") {
            bearerAuth(votante)
            contentType(ContentType.Application.Json)
            setBody(voto)
        }

    private suspend fun assertError(status: HttpStatusCode, codigo: String, respuesta: HttpResponse) {
        assertEquals(status, respuesta.status, respuesta.bodyAsText())
        assertEquals(codigo, respuesta.body<ErrorDto>().codigo)
    }

    private fun EleccionDto.candidatura(denominacion: String) = candidaturas.first { it.denominacion == denominacion }.id

    private companion object {
        const val ADMIN = "admin"
    }
}
