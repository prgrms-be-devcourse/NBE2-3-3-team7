package com.project.popupmarket.exception.oauth2;

import com.project.popupmarket.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
  private final ErrorCode errorCode;

  public AuthenticationException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
