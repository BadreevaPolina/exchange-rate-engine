package com.example.di

import com.example.data.impl.BybitAPIImpl
import com.example.domain.interfaces.BybitAPI
import com.example.data.errors.ErrorHandler
import com.example.data.errors.ErrorMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module


val networkModule = module {
    single<HttpClient> {
        HttpClient(CIO) {
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
            expectSuccess = true
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
    single<ErrorMapper> { ErrorMapper() }
    single<ErrorHandler> { ErrorHandler(get()) }
    factory<BybitAPI> { BybitAPIImpl(get(), get()) }
}