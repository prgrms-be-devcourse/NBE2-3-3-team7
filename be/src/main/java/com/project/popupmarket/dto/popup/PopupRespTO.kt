package com.project.popupmarket.dto.popup

import com.project.popupmarket.dto.popup.PopupTO

data class PopupRespTO(
    var popup: PopupTO? = null, // RentalLandTO 객체
    var thumbnail: String? = null,       // 썸네일 URL
    var images: List<String>? = null     // 이미지 리스트
)
