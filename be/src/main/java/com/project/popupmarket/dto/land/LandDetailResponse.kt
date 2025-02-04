package com.project.popupmarket.dto.land

import com.project.popupmarket.dto.payment.RangeDateTO

data class LandDetailResponse(
    val period: List<RangeDateTO>,
    val data: RentalLandRespTO
)
