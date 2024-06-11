package com.example.data.errors

import com.example.domain.models.requests.Fiat


class SuitableItemNotFoundException(fiat: Fiat) :
    Exception("Suitable item not found for currency: $fiat")
