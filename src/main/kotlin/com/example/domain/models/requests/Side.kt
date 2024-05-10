package com.example.domain.models.requests

enum class Side(private val id: String) {
    BUY("0"),
    SELL("1");

    override fun toString(): String {
        return id
    }
}