package com.example.domain.models.requests

@kotlinx.serialization.Serializable
data class BybitPeerToPeerRequest(
    val userId: String = "",
    val tokenId: String = "USDT",
    val currencyId: String,
    val payment: List<String> = emptyList(),
    val side: String,
    val size: String = "5",
    val page: String = "1",
    val amount: String = "",
    val authMaker: Boolean = false,
    val canTrade: Boolean = false
)
