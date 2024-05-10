package com.example.data

import com.example.data.objects.HttpClient.client
import com.example.domain.models.requests.BybitP2PRequest
import com.example.domain.models.requests.Fiat
import com.example.domain.models.requests.Side
import com.example.domain.models.responses.Item
import com.example.utils.ErrorCode
import kotlinx.coroutines.*

class CurrencyExchange {

    companion object {
        private val p2pService = BybitServiceImpl(client)

        suspend fun calculateExchangeRate(fromCurrency: Fiat, toCurrency: Fiat? = null) = coroutineScope {
            val rateFromDeferred = async { getExchangeRate(fromCurrency, Side.BUY) }
            val rateToDeferred = toCurrency?.let { async { getExchangeRate(it, Side.SELL) } }

            val rateFrom = rateFromDeferred.await()
            val rateTo = rateToDeferred?.await() ?: 1.0

            if (rateFrom > 0 && rateTo > 0) {
                println("${fromCurrency.name} -> ${toCurrency?.name ?: "USDT"}: $rateFrom -> $rateTo")
            } else {
                logError(fromCurrency, toCurrency, rateFrom, rateTo)
            }
        }

        private suspend fun getExchangeRate(fiat: Fiat, side: Side, size: Int = 5): Double =
            withContext(Dispatchers.IO) {
                if (size > 10) {
                    return@withContext -2.0
                }
                val p2pRequestInstance = BybitP2PRequest(
                    currencyId = fiat.toString(),
                    side = side.toString(),
                    size = size.toString()
                )
                val postResponse = p2pService.p2pRequest(p2pRequestInstance)

                if (postResponse?.result?.items.isNullOrEmpty()) {
                    return@withContext -1.0
                }
                val item = findFirstSuitableItem(postResponse?.result?.items)
                item?.price?.toDouble() ?: getExchangeRate(fiat, side, size + 5)
            }

        private fun findFirstSuitableItem(items: List<Item>?): Item? {
            return items?.firstOrNull { item ->
                item.payments.any { payment -> payment != "90" } // Cash in Person
            }
        }

        private fun logError(fromCurrency: Fiat, toCurrency: Fiat?, rateFrom: Double, rateTo: Double) {
            val errorCode = when {
                rateFrom == -1.0 || rateTo == -1.0 -> ErrorCode.EXCHANGE_RATE_RETRIEVAL_FAILED
                rateFrom == -2.0 || rateTo == -2.0 -> ErrorCode.CASH_ONLY_FIRST_PAGE
                else -> null
            }

            errorCode?.let { code ->
                val failedCurrency = when {
                    rateFrom < 0.0 && rateTo < 0.0 -> "${fromCurrency.name} and ${toCurrency?.name ?: ""}"
                    rateFrom < 0.0 -> fromCurrency.name
                    else -> toCurrency?.name ?: ""
                }
                LoggerConfig.logError(code, failedCurrency)
            }
        }

    }
}
