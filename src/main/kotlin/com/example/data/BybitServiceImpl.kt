package com.example.data

import com.example.data.objects.HttpRoutes
import com.example.domain.models.requests.BybitP2PRequest
import com.example.domain.models.responses.BybitP2PResponse
import com.example.domain.interfaces.BybitServiceInterface
import com.example.utils.ErrorCode
import com.example.utils.InfoCode
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class BybitServiceImpl(private val client: HttpClient) : BybitServiceInterface {

    override suspend fun p2pRequest(bybitP2PRequest: BybitP2PRequest): BybitP2PResponse? = try {
        client.post(HttpRoutes.BASE_URL) {
            setBody(bybitP2PRequest)
        }.let { response ->
            when (response.status) {
                HttpStatusCode.OK -> Json.decodeFromString(BybitP2PResponse.serializer(), response.bodyAsText()).also {
                    LoggerConfig.logInfo(InfoCode.TRANSACTION_SUCCESS.message)
                }

                else -> {
                    LoggerConfig.logError(
                        ErrorCode.REQUEST_FAILED,
                        "Status: ${response.status.value} ${response.status.description}"
                    )
                    null
                }
            }
        }
    } catch (e: Exception) {
        logError(e)
        null
    }

    private fun logError(e: Exception) {
        val errorInfo = when (e) {
            is ClientRequestException -> ErrorCode.CLIENT_ERROR to e.response.status
            is ServerResponseException -> ErrorCode.SERVER_ERROR to e.response.status
            else -> ErrorCode.UNEXPECTED_ERROR to null
        }

        LoggerConfig.logError(
            errorInfo.first,
            "Exception: ${errorInfo.second?.value ?: ""} ${errorInfo.second?.description ?: e.localizedMessage}",
            e
        )
    }


}
