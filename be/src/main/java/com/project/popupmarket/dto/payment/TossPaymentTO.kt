package com.project.popupmarket.dto.payment

import java.math.BigDecimal

data class TossPaymentTO(
    val paymentKey: String,
    val orderId: String,
    val amount: BigDecimal
)