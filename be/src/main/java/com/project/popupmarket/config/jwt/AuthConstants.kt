package com.project.popupmarket.config.jwt

import java.time.Duration

object AuthConstants {
    const val JWT_TOKEN_COOKIE_NAME: String = "jwt_token"
    val JWT_TOKEN_DURATION: Duration = Duration.ofDays(14)
    val ACCESS_TOKEN_DURATION: Duration = Duration.ofDays(1)
}
