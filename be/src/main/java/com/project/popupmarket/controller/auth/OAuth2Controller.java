package com.project.popupmarket.controller.auth;

import com.project.popupmarket.dto.oauth.LoginResponse;
import com.project.popupmarket.dto.oauth.OAuthSignupRequest;
import com.project.popupmarket.dto.oauth.OAuthSignupRequiredResponse;
import com.project.popupmarket.dto.token.CreateAccessTokenResponse;
import com.project.popupmarket.exception.ErrorResponse;
import com.project.popupmarket.exception.oauth2.AuthenticationException;
import com.project.popupmarket.exception.oauth2.SignupException;
import com.project.popupmarket.service.oauth.OAuth2Service;
import com.project.popupmarket.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static com.project.popupmarket.config.jwt.AuthConstants.JWT_TOKEN_COOKIE_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class OAuth2Controller {
    private final OAuth2Service oauth2Service;

    @PostMapping("/oauth2/callback")
    public ResponseEntity<?> oAuth2Callback(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            HttpServletResponse response) {
        try {
            String email = oAuth2User.getAttribute("email");
            LoginResponse loginResponse = oauth2Service.handleOAuth2Login(email);

            if (loginResponse.isRequireSignup()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new OAuthSignupRequiredResponse("회원가입이 필요합니다.", loginResponse.getSignupUuid()));
            }

            CookieUtil.addCookie(response, JWT_TOKEN_COOKIE_NAME,
                                 loginResponse.getRefreshToken(),
                                 (int) Duration.ofDays(14).toSeconds());

            return ResponseEntity.ok(new CreateAccessTokenResponse(loginResponse.getAccessToken()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage(),
                                            HttpStatus.INTERNAL_SERVER_ERROR.toString()));
        }
    }

    @PostMapping("/oauth2/signup")
    public ResponseEntity<?> oAuth2Signup(
            @RequestBody OAuthSignupRequest request,
            HttpServletResponse response) {
        try {
            LoginResponse loginResponse = oauth2Service.handleOAuth2Signup(request);

            CookieUtil.addCookie(response, JWT_TOKEN_COOKIE_NAME,
                                 loginResponse.getRefreshToken(),
                                 (int) Duration.ofDays(14).toSeconds());

            return ResponseEntity.ok(new CreateAccessTokenResponse(loginResponse.getAccessToken()));
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(e.getErrorCode().getStatus())
                    .body(new ErrorResponse(e.getMessage(),
                                            e.getErrorCode().getStatus().toString()));
        } catch (SignupException e) {
            return ResponseEntity
                    .status(e.getErrorCode().getStatus())
                    .body(new ErrorResponse(e.getMessage(),
                                            e.getErrorCode().getStatus().toString()));
        }
    }
}
