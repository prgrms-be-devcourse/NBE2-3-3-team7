package com.project.popupmarket.dto.land

data class RentalLandRespTO(
    var rentalLand: RentalLandTO? = null, // RentalLandTO 객체
    var thumbnail: String? = null,       // 썸네일 URL
    var images: List<String>? = null     // 이미지 리스트
)

