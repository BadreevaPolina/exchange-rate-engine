package com.example.domain.models

data class ResultExchange(
    val fromCurrency: String,
    val toCurrency: String,
    val fromCurrencyPrice: Double,
    val toCurrencyPrice: Double,
    var toCurrencyAmount: Double? = null,
    var count: Int? = null,
    var rest: Double? = null
)
