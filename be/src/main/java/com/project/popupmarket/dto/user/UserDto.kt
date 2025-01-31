package com.project.popupmarket.dto.user

data class UserDto(
    var id: Long? = null,
    var email: String? = null,
    var name: String? = null,
    var brand: String? = null,
    var profileImage: String? = null,
    var tel: String? = null
)