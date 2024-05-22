package com.example.domain.models.responses

@kotlinx.serialization.Serializable
data class Result(
    val count: Int,
    val items: List<Item>
)