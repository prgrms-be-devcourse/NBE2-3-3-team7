package com.project.popupmarket.dto.payment

import java.math.BigDecimal
import java.time.LocalDate

class StagingRequest (
    val orderId: String,
    val customerId: Long,
    val landId: Long,
    val start: LocalDate,
    val end: LocalDate,
    val amount: BigDecimal
)
