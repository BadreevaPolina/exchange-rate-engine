package com.example.domain.models

data class ResultExchange(
    val fromCurrency: String,
    val toCurrency: String,
    val fromCurrencyPrice: Double,
    val toCurrencyPrice: Double,
    val amount: Double,
    val count: Int,
    val rest: Double
)
