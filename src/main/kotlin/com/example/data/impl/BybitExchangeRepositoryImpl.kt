package com.example.data.impl

import com.example.data.errors.ErrorHandler
import com.example.data.errors.SuitableItemNotFoundException
import com.example.domain.interfaces.BybitAPI
import com.example.domain.interfaces.ExchangeRepository
import com.example.domain.models.ResultExchange
import com.example.domain.models.requests.BybitPeerToPeerRequest
import com.example.domain.models.requests.Fiat
import com.example.domain.models.requests.Side
import com.example.domain.models.responses.BybitPeerToPeerResponse
import com.example.domain.models.responses.Item
import kotlinx.coroutines.*

internal class BybitExchangeRepositoryImpl(
    private val bybitAPI: BybitAPI, private val errorHandler: ErrorHandler
) : ExchangeRepository {
    companion object {
        const val DEFAULT_SIZE = 10
        const val DEFAULT_TO_CURRENCY = "USDT"
        const val DEFAULT_TO_CURRENCY_RATE = 1.0
        const val CASH_IN_PERSON_PAYMENT = "90"
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e -> errorHandler.handleError(e) }

    override suspend fun getExchangeRate(fiat: Fiat, side: String, size: Int): Double {
        val bybitPeerToPeerRequest =
            BybitPeerToPeerRequest(currencyId = fiat.toString(), side = side, size = size.toString())
        val itemsList = bybitAPI.peerToPeerRequest(bybitPeerToPeerRequest)?.toItemsList()
        return itemsList?.findSuitableItem()?.price?.toDouble() ?: throw SuitableItemNotFoundException(fiat)
    }

    private fun BybitPeerToPeerResponse?.toItemsList(): List<Item> =
        this?.result?.items.orEmpty()

    private fun List<Item>.findSuitableItem(): Item? =
        firstOrNull { it.payments.any { payment -> payment != CASH_IN_PERSON_PAYMENT } }

    override suspend fun executeExchange(fromCurrency: Fiat, toCurrency: Fiat?): ResultExchange =
        coroutineScope {
            val fromCurrencyRate = async(exceptionHandler) {
                getExchangeRate(fromCurrency, Side.BUY.toString(), DEFAULT_SIZE)
            }
            val toCurrencyRate = toCurrency?.let {
                async(exceptionHandler) { getExchangeRate(it, Side.SELL.toString(), DEFAULT_SIZE) }
            }
            ResultExchange(
                fromCurrency = fromCurrency.toString(),
                toCurrency = toCurrency?.toString() ?: DEFAULT_TO_CURRENCY,
                fromCurrencyPrice = fromCurrencyRate.await(),
                toCurrencyPrice = toCurrencyRate?.await() ?: DEFAULT_TO_CURRENCY_RATE
            )
        }
}
