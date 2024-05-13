package com.example.domain.models.responses

@kotlinx.serialization.Serializable
data class SymbolInfo(
    val id: String,
    val exchangeId: String,
    val orgId: String,
    val tokenId: String,
    val currencyId: String,
    val status: Int,
    val lowerLimitAlarm: Int,
    val upperLimitAlarm: Int,
    val itemDownRange: String,
    val itemUpRange: String,
    val currencyMinQuote: String,
    val currencyMaxQuote: String,
    val currencyLowerMaxQuote: String,
    val tokenMinQuote: String,
    val tokenMaxQuote: String,
    val kycCurrencyLimit: String,
    val itemSideLimit: Int,
    val buyFeeRate: String,
    val sellFeeRate: String,
    val orderAutoCancelMinute: Int,
    val orderFinishMinute: Int,
    val tradeSide: Int,
    val currency: Currency?,
    val token: Token?
)
