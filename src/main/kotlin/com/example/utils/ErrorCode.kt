package com.example.utils

enum class ErrorCode(val message: String) {
    CLIENT_ERROR("Client error occurred with status:"),
    SERVER_ERROR("Server error occurred with status:"),
    UNEXPECTED_ERROR("An unexpected error occurred during the request:"),
    EXCHANGE_RATE_RETRIEVAL_FAILED("Failed to retrieve exchange rates:"),
    CASH_ONLY_FIRST_PAGE("Only cash available on the first page for:"),
    REQUEST_FAILED("Request failed with status:"),
    COROUTINE_CANCELLED("The coroutine was cancelled:")
}