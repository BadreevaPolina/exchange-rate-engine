package com.example.data.errors

enum class ErrorCode(val message: String) {
    CLIENT_ERROR("Client error occurred with status:"),
    SERVER_ERROR("Server error occurred with status:"),
    UNEXPECTED_ERROR("An unexpected error occurred during the request:"),
    EXCHANGE_RATE_ERROR("Failed to get exchange rate:")
}