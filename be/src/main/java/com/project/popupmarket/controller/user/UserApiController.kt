package com.project.popupmarket.controller.user

import com.project.popupmarket.config.jwt.AuthConstants.JWT_TOKEN_COOKIE_NAME
import com.project.popupmarket.dto.token.CreateAccessTokenResponse
import com.project.popupmarket.dto.user.UserRegisterDto
import com.project.popupmarket.exception.ErrorCode
import com.project.popupmarket.exception.ErrorResponse
import com.project.popupmarket.service.token.TokenService
import com.project.popupmarket.service.user.UserService
import com.project.popupmarket.util.CookieUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/api")
class UserApiController(
    private val userService: UserService,
    private val tokenService: TokenService
) {
    @PostMapping("/signup")
    fun signup(
        @RequestBody request: UserRegisterDto,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return try {
            // 회원 저장 및 ID 반환
            val userId = userService.save(request)

            // 저장된 회원 정보 조회
            val user = userService.findById(userId)

            // JWT 토큰 생성
            val refreshToken = tokenService.createRefreshToken(user)
            val accessToken = tokenService.createAccessToken(user)

            // 쿠키에 리프레시 토큰 저장
            CookieUtil.addCookie(
                response,
                JWT_TOKEN_COOKIE_NAME,
                refreshToken,
                Duration.ofDays(14).seconds.toInt()
            )

            ResponseEntity.ok(CreateAccessTokenResponse(accessToken))
        } catch (e: Exception) {
            ResponseEntity
                .status(ErrorCode.SIGNUP_FAILED.status)
                .body(ErrorResponse(
                    message = ErrorCode.SIGNUP_FAILED.message,
                    code = ErrorCode.SIGNUP_FAILED.name
                ))
        }
    }
}