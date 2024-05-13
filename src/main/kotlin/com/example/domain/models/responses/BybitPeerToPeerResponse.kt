package com.example.domain.models.responses

@kotlinx.serialization.Serializable
data class BybitPeerToPeerResponse(
    val ret_code: Int,
    val ret_msg: String,
    val result: Result?,
    val ext_code: String,
    val ext_info: String?,
    val time_now: String
)

