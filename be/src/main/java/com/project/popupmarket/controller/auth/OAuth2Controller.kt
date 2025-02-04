package com.project.popupmarket.controller.auth

import com.project.popupmarket.config.jwt.AuthConstants
import com.project.popupmarket.dto.oauth.OAuthSignupRequest
import com.project.popupmarket.exception.ErrorResponse
import com.project.popupmarket.exception.oauth2.AuthenticationException
import com.project.popupmarket.exception.oauth2.SignupException
import com.project.popupmarket.service.oauth.OAuth2Service
import com.project.popupmarket.util.CookieUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class OAuth2Controller(
    private val oauth2Service: OAuth2Service,
) {
    private val url = "http://localhost:5173"

    @GetMapping("/oauth2/callback")
    fun oAuth2Callback(
        @AuthenticationPrincipal oAuth2User: OAuth2User?,  // null 허용
        response: HttpServletResponse,
    ) {

        if (oAuth2User == null) {
            println("OAuth2 로그인 실패")
            response.sendRedirect("$url/signin")
            return
        }
        try {
            println("OAuth2 로그인 요청")
            val email = oAuth2User.getAttribute<String>("email")
            val name = oAuth2User.getAttribute<String>("name")
            val loginResponse = oauth2Service.handleOAuth2Login(email)

            val encodedName = URLEncoder.encode(name, "UTF-8")

            if (loginResponse.isRequireSignup) {
                println("회원가입이 필요합니다.")
                response.sendRedirect("$url/auth/callback?uuid=${loginResponse.signupUuid}&email=$email&name=$encodedName") // ✅ 추가 정보 입력 페이지로 이동
                return
            }

            println("로그인 성공")
            CookieUtil.addCookie(
                response, AuthConstants.JWT_TOKEN_COOKIE_NAME,
                loginResponse.refreshToken,
                Duration.ofDays(14).toSeconds().toInt()
            )

            response.sendRedirect("$url/auth/callback?token=${loginResponse.accessToken}")

        } catch (e: Exception) {
            response.sendRedirect("$url/signin")
        }
    }

    @PostMapping("/oauth2/signup")
    fun oAuth2Signup(
        @RequestPart(value = "data") request: OAuthSignupRequest,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?,
        response: HttpServletResponse,
    ): ResponseEntity<*> {
        return try {
//            val loginResponse =
            oauth2Service.handleOAuth2Signup(request, profileImage)
//            CookieUtil.addCookie(
//                response, AuthConstants.JWT_TOKEN_COOKIE_NAME,
//                loginResponse.refreshToken,
//                Duration.ofDays(14).toSeconds().toInt()
//            )

//            ResponseEntity.ok(CreateAccessTokenResponse(loginResponse.accessToken))
            ResponseEntity.ok("")
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