package com.project.popupmarket.dto.payment

import java.math.BigDecimal

data class ReservationInfoResponse(
    val customerId: Long,
    val customerKey: String,
    val customerName: String,
    val customerEmail: String,
    val customerTel: String,
    val landTitle: String,
    val zipcode: String,
    val address: String,
    val addrDetail: String,
    val price: BigDecimal
)