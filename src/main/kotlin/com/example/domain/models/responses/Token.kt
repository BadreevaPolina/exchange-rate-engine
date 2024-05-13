package com.example.domain.models.responses

@kotlinx.serialization.Serializable
data class Token(
    val id: String,
    val exchangeId: String,
    val orgId: String,
    val tokenId: String,
    val scale: Int,
    val sequence: Int
)
