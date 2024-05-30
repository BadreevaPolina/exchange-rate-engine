package com.example.data.impl

import com.example.domain.interfaces.BybitAPI
import com.example.domain.models.requests.BybitPeerToPeerRequest
import com.example.domain.models.responses.BybitPeerToPeerResponse
import com.example.domain.models.requests.Fiat
import io.ktor.http.*
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.test.KoinTest
import kotlin.test.assertEquals
import org.koin.test.inject
import kotlinx.serialization.json.Json
import java.io.File

class BybitAPIImplTest : KoinTest {
    private val api: BybitAPI by inject()
    private val projectDir = System.getProperty("user.dir")
    private val responseOkJson = "$projectDir/src/test/kotlin/com/example/data/impl/json/responseOk.json"
    private val koinTestUtils = BybitAPITestUtils()

    @Test
    fun `peerToPeerRequest should return expected response on success`() = runBlocking {
        koinTestUtils.startTestKoin(HttpStatusCode.OK, File(responseOkJson).readText())
        testPeerToPeerRequest(expectedSuccess = true)
    }

    @Test
    fun `peerToPeerRequest should handle rate limit error`() = runBlocking {
        koinTestUtils.startTestKoin(HttpStatusCode.TooManyRequests)
        testPeerToPeerRequest(expectedSuccess = false)
    }

    @Test
    fun `peerToPeerRequest should handle client error`() = runBlocking {
        koinTestUtils.startTestKoin(HttpStatusCode.BadRequest)
        testPeerToPeerRequest(expectedSuccess = false)
    }

    @Test
    fun `peerToPeerRequest should handle server error`() = runBlocking {
        koinTestUtils.startTestKoin(HttpStatusCode.InternalServerError)
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
        val jsonText = File(responseOkJson).readText()
        return Json.decodeFromString(BybitPeerToPeerResponse.serializer(), jsonText)
    }
}
