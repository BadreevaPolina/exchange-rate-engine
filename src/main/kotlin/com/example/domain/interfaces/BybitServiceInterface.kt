package com.example.domain.interfaces

import com.example.domain.models.requests.BybitP2PRequest
import com.example.domain.models.responses.BybitP2PResponse

interface BybitServiceInterface {
    suspend fun p2pRequest(bybitP2PRequest: BybitP2PRequest): BybitP2PResponse?
}