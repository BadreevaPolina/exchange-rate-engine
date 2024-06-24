package com.example.data.impl

import com.example.domain.interfaces.ExchangeRepository
import com.example.domain.models.ResultExchange
import com.example.domain.models.requests.Fiat
import io.mockk.coEvery
import org.junit.jupiter.api.Test
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

class ExchangeServiceImplTest {
    private val exchangeRepository = mockk<ExchangeRepository>()
    private val fromCurrency = Fiat.LKR
    private val toCurrency = Fiat.RUB
    private val fromCurrencyAmount = 100.0
    private val exchangeService = ExchangeServiceImpl(exchangeRepository)

    @Test
    fun testCalculateExchangeAmount_SuccessfulExchange() = runBlocking {
        val resultExchange = ResultExchange(fromCurrency.toString(), toCurrency.toString(), 15.0, 10.0)
        coEvery { exchangeRepository.executeExchange(fromCurrency, toCurrency) } returns resultExchange

        val calculatedExchange = exchangeService.calculateExchangeAmount(fromCurrency, toCurrency, fromCurrencyAmount)
        assertEquals(6, calculatedExchange.count)
        assertEquals(10.0, calculatedExchange.rest)
        assertEquals(60.0, calculatedExchange.toCurrencyAmount)
    }

    @Test
    fun testCalculateExchangeAmount_SuccessfulExchangeWithoutToCurrency() = runBlocking {
        val resultExchange = ResultExchange(fromCurrency.toString(), "USDT", 15.0, 1.0)
        coEvery { exchangeRepository.executeExchange(fromCurrency) } returns resultExchange

        val calculatedExchange =
            exchangeService.calculateExchangeAmount(fromCurrency, fromCurrencyAmount = fromCurrencyAmount)
        assertEquals(6, calculatedExchange.count)
        assertEquals(10.0, calculatedExchange.rest)
        assertEquals(6.0, calculatedExchange.toCurrencyAmount)
    }

    @Test
    fun testCalculateExchangeAmount_FailedExchange() = runBlocking {
        val resultExchange = ResultExchange(fromCurrency.toString(), toCurrency.toString(), 1000.0, 10.0)
        coEvery { exchangeRepository.executeExchange(fromCurrency, toCurrency) } returns resultExchange

        val calculatedExchange = exchangeService.calculateExchangeAmount(fromCurrency, toCurrency, fromCurrencyAmount)
        assertEquals(0, calculatedExchange.count)
        assertEquals(100.0, calculatedExchange.rest)
        assertEquals(0.0, calculatedExchange.toCurrencyAmount)
    }
}