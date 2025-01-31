package com.project.popupmarket.exception.oauth2

import com.project.popupmarket.exception.ErrorCode

class SignupException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)