package com.project.popupmarket.dto.home

import com.project.popupmarket.dto.land.RentalLandRespTO
import com.project.popupmarket.dto.popup.PopupRespTO

data class HomeItemTO(
    val popup: List<PopupRespTO>,
    val land: List<RentalLandRespTO>
)