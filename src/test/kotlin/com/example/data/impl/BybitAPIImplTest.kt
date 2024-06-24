package com.example.data.impl

import com.example.data.errors.ErrorHandler
import com.example.data.errors.ErrorMapper
import com.example.data.impl.JsonRoutes.RESPONSE_OK_JSON
import com.example.domain.interfaces.BybitAPI
import com.example.domain.models.requests.BybitPeerToPeerRequest
import com.example.domain.models.responses.BybitPeerToPeerResponse
import com.example.domain.models.requests.Fiat
import io.ktor.client.*
import io.ktor.http.*
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.test.KoinTest
import kotlin.test.assertEquals
import org.koin.test.inject
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.io.File

class BybitAPIImplTest : KoinTest {
    private val api: BybitAPI by inject()
    private val projectDir = System.getProperty("user.dir")
    private val testUtils = TestNetworkingUtils()

    private fun startTestKoin(httpStatusCode: HttpStatusCode, responseText: String = "") {
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        startKoin {
            modules(
                module {
                    single<HttpClient> {
                        testUtils.createHttpClient(
                            testUtils.createMockEngine(
                                httpStatusCode,
                                responseText
                            )
                        )
                    }
                    single<ErrorMapper> { ErrorMapper() }
                    single<ErrorHandler> { ErrorHandler(get()) }
                    factory<BybitAPI> { BybitAPIImpl(get(), get()) }
                })
        }
    }

    @Test
    fun `peerToPeerRequest should return expected response on success`() = runBlocking {
        startTestKoin(HttpStatusCode.OK, File(projectDir + RESPONSE_OK_JSON).readText())
        testPeerToPeerRequest(expectedSuccess = true)
    }

    @Test
    fun `peerToPeerRequest should handle rate limit error`() = runBlocking {
        startTestKoin(HttpStatusCode.TooManyRequests)
        testPeerToPeerRequest(expectedSuccess = false)
    }

    @Test
    fun `peerToPeerRequest should handle client error`() = runBlocking {
        startTestKoin(HttpStatusCode.BadRequest)
        testPeerToPeerRequest(expectedSuccess = false)
    }

    @Test
    fun `peerToPeerRequest should handle server error`() = runBlocking {
        startTestKoin(HttpStatusCode.InternalServerError)
        testPeerToPeerRequest(expectedSuccess = false)
    }

    private suspend fun testPeerToPeerRequest(expectedSuccess: Boolean) {
        val result = api.peerToPeerRequest(createRequest())
        if (expectedSuccess) {
            val expectedResponse = loadExpectedResponse()
            assertEquals(expectedResponse, result)
        } else {
            assertNull(result)
        }
    }

    private fun createRequest() = BybitPeerToPeerRequest(
        currencyId = Fiat.IDR.toString(),
        side = "1",
        size = "3"
    )

    private fun loadExpectedResponse(): BybitPeerToPeerResponse {
        val jsonText = File(projectDir + RESPONSE_OK_JSON).readText()
        return Json.decodeFromString(BybitPeerToPeerResponse.serializer(), jsonText)
    }
}
