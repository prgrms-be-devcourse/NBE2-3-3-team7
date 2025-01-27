package com.project.popupmarket.exception.oauth2;

import com.project.popupmarket.exception.ErrorCode;
import lombok.Getter;

@Getter
public class SignupException extends RuntimeException {
    private final ErrorCode errorCode;

    public SignupException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
