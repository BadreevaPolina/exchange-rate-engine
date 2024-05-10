package com.example.domain.models.responses

@kotlinx.serialization.Serializable
data class Currency(
    val id: String,
    val exchangeId: String,
    val orgId: String,
    val currencyId: String,
    val scale: Int
)