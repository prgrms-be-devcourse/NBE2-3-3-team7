package com.project.popupmarket.dto.user

import com.project.popupmarket.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UserRegisterDto(
    @field:NotEmpty(message = "이메일은 필수입니다")
    @field:Email(message = "이메일 형식이 올바르지 않습니다")
    val email: String,

    @field:NotEmpty(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val password: String,

    @field:NotEmpty(message = "이름은 필수입니다")
    val name: String,

    @field:NotEmpty(message = "브랜드명은 필수입니다")
    val brand: String,

    @field:NotEmpty(message = "전화번호는 필수입니다")
    val tel: String,

    @field:NotEmpty(message = "사업자 등록번호는 필수입니다")
    val businessId: String,

    @field:NotEmpty(message = "ROLE은 필수입니다")
    val role: Role
) {
    constructor() : this(
        email = "",
        password = "",
        name = "",
        brand = "",
        tel = "",
        businessId = "",
        role = Role.CUSTOMER
    ) {

    }
}