package com.example

import com.example.data.CurrencyExchange
import com.example.di.exchangeModule
import com.example.domain.models.requests.Fiat
import com.example.utils.ErrorCode
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import kotlin.coroutines.cancellation.CancellationException


fun main() = runBlocking {
    startKoin {
        modules(exchangeModule)
    }
    val job = launch {
        try {
            CurrencyExchange.calculateExchangeRate(Fiat.RUB, Fiat.LKR)
        } catch (e: CancellationException) {
            LoggerConfig.logError(ErrorCode.COROUTINE_CANCELLED, e.message, e)
        }
    }
    job.join()
}
