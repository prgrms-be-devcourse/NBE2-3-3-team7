package com.project.popupmarket.dto.user

import org.springframework.web.multipart.MultipartFile

data class UserUpdateRequest(
    var password: String,
    var name: String,
    var brand: String,
    var tel: String,
    var profileImage: MultipartFile? = null
)