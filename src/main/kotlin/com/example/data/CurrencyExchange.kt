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
            val fromRateDeferred = async { getExchangeRate(fromCurrency, Side.BUY) }
            val toRateDeferred = toCurrency?.let { async { getExchangeRate(it, Side.SELL) } }

            val fromRate = fromRateDeferred.await()
            val toRate = toRateDeferred?.await() ?: 1.0

            if (fromRate > 0 && toRate > 0) {
                println("${fromCurrency.name} -> ${toCurrency?.name ?: "USDT"}: $fromRate -> $toRate")
            } else {
                logError(fromCurrency, toCurrency, fromRate, toRate)
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

        private fun logError(fromCurrency: Fiat, toCurrency: Fiat?, fromRate: Double, toRate: Double) {
            val errorCode = when {
                fromRate == -1.0 || toRate == -1.0 -> ErrorCode.EXCHANGE_RATE_RETRIEVAL_FAILED
                fromRate == -2.0 || toRate == -2.0 -> ErrorCode.CASH_ONLY_FIRST_PAGE
                else -> null
            }

            errorCode?.let { code ->
                val failedCurrency = when {
                    fromRate < 0.0 && toRate < 0.0 -> "${fromCurrency.name} and ${toCurrency?.name ?: ""}"
                    fromRate < 0.0 -> fromCurrency.name
                    else -> toCurrency?.name ?: ""
                }
                LoggerConfig.logError(code, failedCurrency)
            }
        }

    }
}
