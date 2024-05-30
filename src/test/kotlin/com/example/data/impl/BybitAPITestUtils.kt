package com.example.data.impl

import com.example.data.errors.ErrorHandler
import com.example.data.errors.ErrorMapper
import com.example.domain.interfaces.BybitAPI
import io.ktor.http.*
import org.koin.core.context.GlobalContext
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import org.koin.dsl.module
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class BybitAPITestUtils {
    fun startTestKoin(httpStatusCode: HttpStatusCode, responseText: String = "") {
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        startKoin {
            modules(
                module {
                    single<HttpClient> { createHttpClient(createMockEngine(httpStatusCode, responseText)) }
                    single<ErrorMapper> { ErrorMapper() }
                    single<ErrorHandler> { ErrorHandler(get()) }
                    factory<BybitAPI> { BybitAPIImpl(get(), get()) }
                })
        }
    }


    private fun createHttpClient(mockEngine: MockEngine = createMockEngine(HttpStatusCode.OK)) =
        HttpClient(mockEngine) {
            defaultRequest {
                header("Content-Type", "application/json")
            }
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }
        }

    private fun createMockEngine(statusCode: HttpStatusCode, responseText: String = ""): MockEngine {
        return MockEngine { _ ->
            respond(
                content = ByteReadChannel(responseText),
                status = statusCode,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
    }

}
