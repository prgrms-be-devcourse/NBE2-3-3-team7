package com.project.popupmarket.dto.oauth

data class LoginResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val isRequireSignup: Boolean = false,
    val signupUuid: String? = null
) {
    companion object {
        @JvmStatic
        fun requireSignup(uuid: String): LoginResponse {
            return LoginResponse(
                isRequireSignup = true,
                signupUuid = uuid
            )
        }
    }

    constructor(accessToken: String, refreshToken: String) : this(
        accessToken = accessToken,
        refreshToken = refreshToken,
        isRequireSignup = false,
        signupUuid = null
    )
}