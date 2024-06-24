package com.example.data.impl

import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*

class TestNetworkingUtils {
    fun createHttpClient(mockEngine: MockEngine = createMockEngine(HttpStatusCode.OK)) =
        HttpClient(mockEngine) {
            defaultRequest {
                header("Content-Type", "application/json")
            }
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }
        }

    fun createMockEngine(statusCode: HttpStatusCode, responseText: String = ""): MockEngine {
        return MockEngine { _ ->
            respond(
                content = ByteReadChannel(responseText),
                status = statusCode,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
    }

}
