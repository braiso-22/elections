package com.braiso_22.boe.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun getBoe(): BOEResponse? {

    val response: HttpResponse = try {
        val client = HttpClient()
        client.request("https://www.boe.es/datosabiertos/api/boe/sumario/" + "20230621") {
            method = HttpMethod.Get
        }
    } catch (ex: Exception) {
        return null
    }


    if (response.status != HttpStatusCode.OK) {
        return null
    }
    val body: BOEResponse = response.body()
    return body
}