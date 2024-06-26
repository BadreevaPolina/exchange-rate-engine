package com.example.data.errors

import io.ktor.client.plugins.*
import io.ktor.http.*

class ErrorMapper {
        fun parseException(exception: Throwable): Pair<ErrorCode, HttpStatusCode?> {
            return when (exception) {
                is ClientRequestException -> ErrorCode.CLIENT_ERROR to exception.response.status
                is ServerResponseException -> ErrorCode.SERVER_ERROR to exception.response.status
                is SuitableItemNotFoundException -> ErrorCode.EXCHANGE_RATE_ERROR to null
                else -> ErrorCode.UNEXPECTED_ERROR to null
            }
        }

}