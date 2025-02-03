package com.project.popupmarket.dto.payment

import java.math.BigDecimal

data class AnalyticsTO @JvmOverloads constructor(
    val landId: Long,
    val landTitle: String,
    val monthlyRevenue: BigDecimal,
    var status: String = "",
)