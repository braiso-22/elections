package com.braiso_22.elections

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testParties() = testApplication {
        application {
            module()
        }
        client.get("/v1/parties?acronym=PT").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Partido test"))
        }
    }

    @Test
    fun testVote() = testApplication {
        application {
            module()
        }
        client.post("/v1/vote") {
            contentType(ContentType.Application.Json)
            setBody("""{"partyId":"3","userId":"user-1"}""")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        client.get("/v1/votes").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains(""""3":1"""))
        }
    }
}
