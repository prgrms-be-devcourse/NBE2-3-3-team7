package com.project.popupmarket.dto.payment

import org.springframework.data.domain.Page

data class ReservationResponse(
    val landTitle: String,
    val reservation: Page<ReceiptsInfoTO>
)