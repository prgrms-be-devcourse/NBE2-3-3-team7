package com.project.popupmarket.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final boolean requireSignup;
    private final String signupUuid;

    public static LoginResponse requireSignup(String uuid) {
        return new LoginResponse(null, null, true, uuid);
    }

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.requireSignup = false;
        this.signupUuid = null;
    }
}