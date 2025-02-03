package com.project.popupmarket.dto.user

import com.project.popupmarket.dto.land.RentalLandRespTO
import com.project.popupmarket.dto.payment.AnalyticsTO
import com.project.popupmarket.dto.payment.ReceiptsInfoTO
import com.project.popupmarket.dto.popup.PopupRespTO

data class UserHomeResponse(
    val user: UserDto,
    val land: List<RentalLandRespTO>,
    val analytics: List<AnalyticsTO>,
    val popup: List<PopupRespTO>,
    val payment: List<ReceiptsInfoTO>
)
