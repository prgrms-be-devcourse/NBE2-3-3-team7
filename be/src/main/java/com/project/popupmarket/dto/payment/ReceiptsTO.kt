package com.project.popupmarket.dto.payment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReceiptsTO(
    val paymentKey: String,
    val orderId: String,
    var customerId: Long,
    val landId: Long,
    val start: LocalDate,
    val end: LocalDate,
    val amount: BigDecimal
)
