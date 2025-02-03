package com.project.popupmarket.dto.user

import com.project.popupmarket.enums.Role

data class UserDto(
    var id: Long? = null,
    var email: String? = null,
    var name: String? = null,
    var brand: String? = null,
    var role : Role? = null,
    var profileImage: String? = null,
    var tel: String? = null,
    var businessId: String? = null
)