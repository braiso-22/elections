package com.braiso_22.elections.boe.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

suspend fun getBoe(): BOEResponse? {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    return client.use {
        try {
            val response: HttpResponse = it.request("https://www.boe.es/datosabiertos/api/boe/sumario/" + "20230621") {
                method = HttpMethod.Get
                accept(ContentType.Application.Json)
            }

            if (response.status != HttpStatusCode.OK) {
                return null
            }
            val body: BOEResponse = response.body()
            body
        } catch (ex: Exception) {
            null
        }
    }
}
