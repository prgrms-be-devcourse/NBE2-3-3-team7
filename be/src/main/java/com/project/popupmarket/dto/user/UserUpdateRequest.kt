package com.project.popupmarket.dto.user

import org.springframework.web.multipart.MultipartFile

data class UserUpdateRequest(
    var password: String,
    var brand: String,
    var profileImage: MultipartFile? = null
)