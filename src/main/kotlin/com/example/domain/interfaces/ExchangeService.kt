package com.example.domain.interfaces

import com.example.domain.models.ResultExchange
import com.example.domain.models.requests.Fiat

interface ExchangeService {
    fun calculateExchangeAmount(fromCurrency: Fiat, toCurrency: Fiat? = null, amount: Double): ResultExchange
}