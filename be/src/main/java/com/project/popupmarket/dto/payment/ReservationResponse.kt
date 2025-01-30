package com.project.popupmarket.dto.payment

data class ReservationResponse(
    val landTitle: String,
    val reservation: List<ReceiptsInfoTO>
)