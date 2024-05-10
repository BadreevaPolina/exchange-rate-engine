package com.example.domain.models.requests

enum class Fiat(private val id: String) {
    RUB("RUB"),
    LKR("LKR"),
    IDR("IDR");

    override fun toString(): String {
        return id
    }
}