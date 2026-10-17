package com.ipinfo.app

data class IPHistoryItem(
    val ipAddress: String,
    val location: String,
    val timestamp: Long,
    val isValid: Boolean,
    val reliability: Float
)