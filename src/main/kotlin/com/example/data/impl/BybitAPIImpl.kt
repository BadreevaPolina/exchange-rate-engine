package com.example.data.impl

import com.example.data.errors.ErrorHandler
import com.example.data.objects.HttpRoutes
import com.example.domain.interfaces.BybitAPI
import com.example.domain.models.requests.BybitPeerToPeerRequest
import com.example.domain.models.responses.BybitPeerToPeerResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

internal class BybitAPIImpl(private val client: HttpClient, private val errorHandler: ErrorHandler) : BybitAPI {
    override suspend fun peerToPeerRequest(bybitPeerToPeerRequest: BybitPeerToPeerRequest): BybitPeerToPeerResponse? =
        try {
            client.post(HttpRoutes.BYBIT_URL) { setBody(bybitPeerToPeerRequest) }.handleResponse()
        } catch (e: Exception) {
            errorHandler.handleError(e)
        }

    private suspend fun HttpResponse.handleResponse(): BybitPeerToPeerResponse? {
        return if (this.status == HttpStatusCode.OK) {
            Json.decodeFromString(BybitPeerToPeerResponse.serializer(), this.bodyAsText())
        } else {
            null
        }
    }

}