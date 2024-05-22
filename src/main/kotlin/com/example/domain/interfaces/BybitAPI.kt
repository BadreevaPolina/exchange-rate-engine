package com.example.domain.interfaces

import com.example.domain.models.requests.BybitPeerToPeerRequest
import com.example.domain.models.responses.BybitPeerToPeerResponse

interface BybitAPI {
    suspend fun peerToPeerRequest(bybitPeerToPeerRequest: BybitPeerToPeerRequest): BybitPeerToPeerResponse?
}