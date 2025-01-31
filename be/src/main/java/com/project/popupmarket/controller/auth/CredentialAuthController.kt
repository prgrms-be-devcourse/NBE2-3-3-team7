package com.project.popupmarket.controller.auth

import com.project.popupmarket.config.jwt.AuthConstants
import com.project.popupmarket.dto.auth.LoginRequest
import com.project.popupmarket.dto.token.CreateAccessTokenResponse
import com.project.popupmarket.entity.User
import com.project.popupmarket.exception.ErrorCode
import com.project.popupmarket.exception.ErrorResponse
import com.project.popupmarket.service.token.TokenService
import com.project.popupmarket.service.user.UserService
import com.project.popupmarket.util.CookieUtil
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
class CredentialAuthController {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenService: TokenService

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        ignoredRequest: HttpServletRequest?,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        // 1. 사용자 인증
        val authenticatedUser: User
        try {
            authenticatedUser = userService!!.authenticate(request)
        } catch (e: Exception) {
            System.out.println("error = " + e);
            return ResponseEntity
                .status(ErrorCode.INVALID_CREDENTIALS.status)
                .body(
                    ErrorResponse(
                        ErrorCode.INVALID_CREDENTIALS.message, ErrorCode.INVALID_CREDENTIALS
                            .status
                            .toString()
                    )
                )
        }

        // 2. 토큰 생성
        val accessToken = tokenService!!.createAccessToken(authenticatedUser)

        //        System.out.println("accessToken = " + accessToken);
        val refreshToken = tokenService.createRefreshToken(authenticatedUser)

        //        System.out.println("refreshToken = " + refreshToken);

        // 3. 리프레시 토큰을 쿠키에 저장
        CookieUtil.addCookie(
            response, AuthConstants.JWT_TOKEN_COOKIE_NAME, refreshToken, Duration
                .ofDays(14)
                .toSeconds().toInt()
        )

        // 4. 액세스 토큰 반환
        return ResponseEntity.ok(CreateAccessTokenResponse(accessToken))
    }

    // 로그아웃
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any?> {
        return CookieUtil
            .getCookie(request, AuthConstants.JWT_TOKEN_COOKIE_NAME)
            .map { cookie: Cookie ->
                tokenService!!.deleteRefreshToken(tokenService.getUserIdFromToken(cookie.value))
                CookieUtil.deleteCookie(request, response, AuthConstants.JWT_TOKEN_COOKIE_NAME)
                ResponseEntity
                    .ok()
                    .build<Any?>()
            }
            .orElse(
                ResponseEntity
                    .badRequest()
                    .build()
            )
    }
}