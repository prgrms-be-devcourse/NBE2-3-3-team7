package com.project.popupmarket.exception.oauth2

import com.project.popupmarket.exception.ErrorCode

class AuthenticationException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)