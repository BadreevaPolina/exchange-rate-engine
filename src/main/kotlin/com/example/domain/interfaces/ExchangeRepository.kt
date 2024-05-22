package com.example.domain.interfaces

import com.example.domain.models.ResultExchange
import com.example.domain.models.requests.Fiat


interface ExchangeRepository {
    suspend fun getExchangeRate(fiat: Fiat, side: String, size: Int): Double
    suspend fun executeExchange(fromCurrency: Fiat, toCurrency: Fiat? = null): ResultExchange
}