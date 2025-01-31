package com.project.popupmarket.controller.auth

import com.project.popupmarket.config.jwt.AuthConstants
import com.project.popupmarket.dto.oauth.OAuthSignupRequest
import com.project.popupmarket.dto.oauth.OAuthSignupRequiredResponse
import com.project.popupmarket.dto.token.CreateAccessTokenResponse
import com.project.popupmarket.exception.ErrorResponse
import com.project.popupmarket.exception.oauth2.AuthenticationException
import com.project.popupmarket.exception.oauth2.SignupException
import com.project.popupmarket.service.oauth.OAuth2Service
import com.project.popupmarket.util.CookieUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class OAuth2Controller(
    @Autowired
    private val oauth2Service: OAuth2Service
) {
    @GetMapping("/oauth2/callback")
    fun oAuth2Callback(
        @AuthenticationPrincipal oAuth2User: OAuth2User?,  // null 허용
        response: HttpServletResponse
    ): ResponseEntity<*> {
        if (oAuth2User == null) {
            println("OAuth2 로그인 실패")
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse("인증 실패", HttpStatus.UNAUTHORIZED.toString()))
        }
        return try {
            println("OAuth2 로그인 요청")
            val email = oAuth2User.getAttribute<String>("email")
            val loginResponse = oauth2Service.handleOAuth2Login(email)

            if (loginResponse.isRequireSignup) {
                println("회원가입이 필요합니다.")
                ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(OAuthSignupRequiredResponse("회원가입이 필요합니다.", loginResponse.signupUuid))
            } else {
                println("로그인 성공")
                CookieUtil.addCookie(
                    response, AuthConstants.JWT_TOKEN_COOKIE_NAME,
                    loginResponse.refreshToken,
                    Duration.ofDays(14).toSeconds().toInt()
                )

                ResponseEntity.ok(CreateAccessTokenResponse(loginResponse.accessToken))
            }
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse(e.message, HttpStatus.INTERNAL_SERVER_ERROR.toString()))
        }
    }

    @PostMapping("/oauth2/signup")
    fun oAuth2Signup(
        @RequestBody request: OAuthSignupRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return try {
            val loginResponse = oauth2Service.handleOAuth2Signup(request)

            CookieUtil.addCookie(
                response, AuthConstants.JWT_TOKEN_COOKIE_NAME,
                loginResponse.refreshToken,
                Duration.ofDays(14).toSeconds().toInt()
            )

            ResponseEntity.ok(CreateAccessTokenResponse(loginResponse.accessToken))
        } catch (e: AuthenticationException) {
            ResponseEntity
                .status(e.errorCode.status)
                .body(ErrorResponse(e.message, e.errorCode.status.toString()))
        } catch (e: SignupException) {
            ResponseEntity
                .status(e.errorCode.status)
                .body(ErrorResponse(e.message, e.errorCode.status.toString()))
        }
    }
}