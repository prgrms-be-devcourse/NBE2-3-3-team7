package com.project.popupmarket.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthSignupRequiredResponse {
    String message;
    String uuid;
}
