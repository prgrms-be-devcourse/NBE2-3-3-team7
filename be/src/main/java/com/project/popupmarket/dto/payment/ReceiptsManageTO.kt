package com.project.popupmarket.dto.payment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.project.popupmarket.enums.ReservationStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReceiptsManageTO(
    val paymentKey: String,
    val amount: BigDecimal,
    val start: LocalDate,
    val end: LocalDate,
    val status: ReservationStatus,
    val reservedAt: LocalDateTime
)
