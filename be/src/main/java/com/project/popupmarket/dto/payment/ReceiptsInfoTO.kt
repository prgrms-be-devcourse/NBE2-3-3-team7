package com.project.popupmarket.dto.payment

import java.math.BigDecimal
import java.time.LocalDate

data class ReceiptsInfoTO(
    val orderId: String,
    val status: String,
    val landTitle: String,
    val customerName: String,
    val price: BigDecimal,
    val amount: BigDecimal,
    val start: LocalDate,
    val end: LocalDate
)