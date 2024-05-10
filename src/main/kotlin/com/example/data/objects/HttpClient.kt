package com.example.data.objects

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClient {
    val client = HttpClient(CIO) {
        defaultRequest {
            header("Accept-Encoding", "gzip, deflate, br")
            header("Accept", "*/*")
            header("Connection", "keep-alive")
            header("Content-Type", "application/json")
            header("Origin", "https://www.bybit.com")
            header("Referer", "https://www.bybit.com/")
            header("cache-control", "no-cache")
            header("pragma", "no-cache")
        }
        install(ContentEncoding) {
            gzip()
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
