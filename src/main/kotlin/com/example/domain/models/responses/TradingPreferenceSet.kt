package com.example.domain.models.responses


@kotlinx.serialization.Serializable
data class TradingPreferenceSet(
    val hasUnPostAd: Int,
    val isKyc: Int,
    val isEmail: Int,
    val isMobile: Int,
    val hasRegisterTime: Int,
    val registerTimeThreshold: Int,
    val orderFinishNumberDay30: Int,
    val completeRateDay30: String,
    val nationalLimit: String,
    val hasOrderFinishNumberDay30: Int,
    val hasCompleteRateDay30: Int,
    val hasNationalLimit: Int
)