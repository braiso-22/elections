package com.braiso_22.elections

import com.braiso_22.elections.parties.data.InMemoryPartyRepository
import com.braiso_22.elections.parties.data.InMemoryVoteRepository
import com.braiso_22.elections.parties.data.RoomPartyRepository
import com.braiso_22.elections.parties.data.RoomVoteRepository
import com.braiso_22.elections.parties.data.defaultParties
import com.braiso_22.elections.parties.data.room.createInMemoryElectionsDatabase
import com.braiso_22.elections.parties.data.room.use
import com.braiso_22.elections.parties.presentation.dtos.PoliticalPartyDTO
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testRoot() = testInMemoryApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testParties() = testInMemoryApplication {
        client.get("/v1/parties?acronym=PT").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Partido test"))
        }
    }

    @Test
    fun testAllPartiesWithoutParams() = testInMemoryApplication {
        client.get("/v1/parties").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(defaultParties.map { it.id }, parties().map { it.id })
        }
    }

    @Test
    fun testPartyById() = testInMemoryApplication {
        client.get("/v1/party/3").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Partido test", Json.decodeFromString<PoliticalPartyDTO>(bodyAsText()).name)
        }
    }

    @Test
    fun testPartyByIdNotFound() = testInMemoryApplication {
        client.get("/v1/party/99").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            assertEquals("Party not found", bodyAsText())
        }
    }

    @Test
    fun testVote() = testInMemoryApplication {
        client.vote(partyId = "3", userId = "user-1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        client.get("/v1/votes").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(1, voteCounts()["3"])
        }
    }

    @Test
    fun testVoteForUnknownParty() = testInMemoryApplication {
        client.vote(partyId = "99", userId = "user-1").apply {
            assertEquals(HttpStatusCode.ExpectationFailed, status)
            assertEquals("Party not found", bodyAsText())
        }
        client.get("/v1/votes").apply {
            assertTrue(voteCounts().values.all { it == 0 })
        }
    }

    @Test
    fun testVotingAgainReplacesThePreviousVote() = testInMemoryApplication {
        client.vote(partyId = "3", userId = "user-1")
        client.vote(partyId = "4", userId = "user-1")

        client.get("/v1/votes").apply {
            val counts = voteCounts()
            assertEquals(0, counts["3"])
            assertEquals(1, counts["4"])
        }
    }

    @Test
    fun testVoteWithRoomDatabase() = createInMemoryElectionsDatabase().use { database ->
        testApplication {
            application {
                module(
                    partyRepository = RoomPartyRepository(database.partyDao()),
                    voteRepository = RoomVoteRepository(database.voteDao()),
                )
            }
            client.vote(partyId = "3", userId = "user-1")
            client.vote(partyId = "5", userId = "user-2")

            client.get("/v1/votes").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals(
                    mapOf("1" to 0, "2" to 0, "3" to 1, "4" to 0, "5" to 1, "6" to 0, "7" to 0),
                    voteCounts()
                )
            }
        }
    }

    private fun testInMemoryApplication(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            module(
                partyRepository = InMemoryPartyRepository(),
                voteRepository = InMemoryVoteRepository(),
            )
        }
        block()
    }

    private suspend fun HttpClient.vote(partyId: String, userId: String): HttpResponse =
        post("/v1/vote") {
            contentType(ContentType.Application.Json)
            setBody("""{"partyId":"$partyId","userId":"$userId"}""")
        }

    private suspend fun HttpResponse.parties(): List<PoliticalPartyDTO> =
        Json.decodeFromString(bodyAsText())

    private suspend fun HttpResponse.voteCounts(): Map<String, Int> =
        Json.decodeFromString(bodyAsText())
}
