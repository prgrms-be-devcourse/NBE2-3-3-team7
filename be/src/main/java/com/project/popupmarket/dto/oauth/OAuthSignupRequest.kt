package com.project.popupmarket.dto.oauth

import com.project.popupmarket.enums.Role
import jakarta.validation.constraints.NotEmpty

data class OAuthSignupRequest(
    @field:NotEmpty(message = "uuid는 필수입니다")
    var uuid: String,

    @field:NotEmpty(message = "이름은 필수입니다")
    var name: String,

    @field:NotEmpty(message = "브랜드명은 필수입니다")
    var brand: String,

    @field:NotEmpty(message = "전화번호는 필수입니다")
    var tel: String,

    @field:NotEmpty(message = "사업자 등록번호는 필수입니다")
    var businessId: String,

    @field:NotEmpty(message = "ROLE은 필수입니다")
    var role: Role
)