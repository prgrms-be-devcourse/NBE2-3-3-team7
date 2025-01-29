package com.project.popupmarket.dto.payment

import java.time.LocalDate

data class ReservationTO(
    val customerId: Long,
    val landId: Long,
    val start: LocalDate,
    val end: LocalDate
)