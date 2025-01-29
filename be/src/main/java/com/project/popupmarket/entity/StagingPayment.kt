package com.project.popupmarket.entity

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate

data class StagingPayment(
    val orderId: String,
    val customerId: Long,
    val rentalLandId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalAmount: BigDecimal,
) : Serializable