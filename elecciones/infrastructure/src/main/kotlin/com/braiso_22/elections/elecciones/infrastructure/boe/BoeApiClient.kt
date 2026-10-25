package com.braiso_22.elections.elecciones.infrastructure.boe

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private val logger = LoggerFactory.getLogger("BOE")

/**
 * boe.es is signed by the Spanish FNMT root CA, which is missing from many JDK truststores,
 * so it is added on top of the default trusted certificates.
 */
private val boeTrustManager: X509TrustManager by lazy {
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply { load(null, null) }

    val defaultFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    defaultFactory.init(null as KeyStore?)
    defaultFactory.trustManagers.filterIsInstance<X509TrustManager>()
        .flatMap { it.acceptedIssuers.toList() }
        .forEachIndexed { index, cert -> keyStore.setCertificateEntry("default-$index", cert) }

    val fnmtRoot = object {}.javaClass.getResourceAsStream("/ac-raiz-fnmt-rcm.pem")!!.use {
        CertificateFactory.getInstance("X.509").generateCertificate(it) as X509Certificate
    }
    keyStore.setCertificateEntry("ac-raiz-fnmt-rcm", fnmtRoot)

    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        .apply { init(keyStore) }
        .trustManagers.filterIsInstance<X509TrustManager>().first()
}

/**
 * Cliente de la API de datos abiertos del BOE. Todavía no implementa el puerto BoletinOficial:
 * falta localizar en el sumario la convocatoria y las candidaturas proclamadas.
 */
class BoeApiClient(
    private val client: HttpClient = HttpClient(CIO) {
        engine {
            https {
                trustManager = boeTrustManager
            }
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    },
) : AutoCloseable {

    /** @param fecha en formato AAAAMMDD. Devuelve null si el BOE no responde con un sumario. */
    suspend fun sumario(fecha: String): BOEResponse? = try {
        val response: HttpResponse = client.get("https://www.boe.es/datosabiertos/api/boe/sumario/$fecha") {
            accept(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.OK) response.body() else {
            logger.warn("El BOE respondió {} al pedir el sumario del {}", response.status, fecha)
            null
        }
    } catch (ex: Exception) {
        logger.error("Error fetching BOE sumario", ex)
        null
    }

    override fun close() = client.close()
}
