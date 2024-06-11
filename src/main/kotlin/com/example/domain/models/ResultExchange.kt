package com.example.domain.models

data class ResultExchange(
    val fromCurrency: String,
    val toCurrency: String,
    val fromCurrencyPrice: Double,
    val toCurrencyPrice: Double,
    val amount: Double? = null,
    val count: Int? = null,
    val rest: Double? = null
)
