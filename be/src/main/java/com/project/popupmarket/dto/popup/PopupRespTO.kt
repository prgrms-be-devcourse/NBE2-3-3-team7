package com.project.popupmarket.dto.popup;

data class PopupRespTO(
    var popup: PopupTO? = null,
    var thumbnail: String? = null,
    var images: List<String>?= null
)