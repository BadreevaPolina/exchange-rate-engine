package com.example.data.impl

import com.example.domain.interfaces.ExchangeRepository
import com.example.domain.interfaces.ExchangeService
import com.example.domain.models.ResultExchange
import com.example.domain.models.requests.Fiat

class ExchangeServiceImpl(private val exchangeRepository: ExchangeRepository) : ExchangeService {
    override suspend fun calculateExchangeAmount(
        fromCurrency: Fiat, toCurrency: Fiat?, fromCurrencyAmount: Double
    ): ResultExchange {
        val resultExchange = exchangeRepository.executeExchange(fromCurrency, toCurrency)
        resultExchange.updateExchangeDetails(fromCurrencyAmount)
        return resultExchange
    }

    private fun ResultExchange.updateExchangeDetails(fromCurrencyAmount: Double) {
        count = calculateCount(fromCurrencyAmount)
        rest = calculateRest(fromCurrencyAmount)
        toCurrencyAmount = count?.times(toCurrencyPrice)
    }

    private fun ResultExchange.calculateCount(fromCurrencyAmount: Double): Int {
        return (fromCurrencyAmount / fromCurrencyPrice).toInt()
    }

    private fun ResultExchange.calculateRest(fromCurrencyAmount: Double): Double {
        return fromCurrencyAmount - (count?.times(fromCurrencyPrice) ?: 0.0)
    }
}