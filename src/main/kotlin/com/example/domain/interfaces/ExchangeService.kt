package com.example.domain.interfaces

import com.example.domain.models.ResultExchange
import com.example.domain.models.requests.Fiat

interface ExchangeService {
    suspend fun calculateExchangeAmount(
        fromCurrency: Fiat,
        toCurrency: Fiat? = null,
        fromCurrencyAmount: Double
    ): ResultExchange
}