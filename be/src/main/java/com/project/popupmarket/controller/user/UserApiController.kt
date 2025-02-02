package com.project.popupmarket.controller.user

import com.project.popupmarket.config.jwt.AuthConstants.JWT_TOKEN_COOKIE_NAME
import com.project.popupmarket.dto.token.CreateAccessTokenResponse
import com.project.popupmarket.dto.user.UserDto
import com.project.popupmarket.dto.user.UserRegisterDto
import com.project.popupmarket.dto.user.UserUpdateRequest
import com.project.popupmarket.exception.ErrorCode
import com.project.popupmarket.exception.ErrorResponse
import com.project.popupmarket.service.token.TokenService
import com.project.popupmarket.service.user.UserService
import com.project.popupmarket.util.CookieUtil
import jakarta.servlet.http.HttpServletResponse
import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.Duration

@RestController
@RequestMapping("/api")
class UserApiController(
    private val userService: UserService,
    private val tokenService: TokenService
) {
    @PostMapping("/signup")
    fun signup(
        @RequestPart(value = "data") request: UserRegisterDto,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return try {
            // 회원 저장 및 ID 반환
//            val userId =
            userService.save(request, profileImage)
//            // 저장된 회원 정보 조회
//            val user = userService.findById(userId)
//
//            // JWT 토큰 생성
//            val refreshToken = tokenService.createRefreshToken(user)
//            val accessToken = tokenService.createAccessToken(user)

//            // 쿠키에 리프레시 토큰 저장
//            CookieUtil.addCookie(
//                response,
//                JWT_TOKEN_COOKIE_NAME,
//                refreshToken,
//                Duration.ofDays(14).seconds.toInt()
//            )

//            ResponseEntity.ok(CreateAccessTokenResponse(accessToken))
            ResponseEntity.ok("")
        } catch (e: Exception) {
            ResponseEntity
                .status(ErrorCode.SIGNUP_FAILED.status)
                .body(ErrorResponse(
                    message = ErrorCode.SIGNUP_FAILED.message,
                    code = ErrorCode.SIGNUP_FAILED.name
                ))
        }
    }

    @GetMapping("/user")
    fun getUserInfo(): ResponseEntity<UserDto> {
        return ResponseEntity.ok(getCurrentUser())
    }

    @PutMapping("/user")
    fun updateUser(
        @RequestPart(value = "data") request: UserUpdateRequest,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?
    ): ResponseEntity<String> {
        return try {
            request.profileImage = profileImage
            userService.updateUser(getCurrentUser().id!!, request)
            ResponseEntity.ok("")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/user")
    fun deleteUser(): ResponseEntity<String> {
        return try {
            userService.deleteUser(getCurrentUser().id!!)
            ResponseEntity.ok("")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    private fun getCurrentUser(): UserDto {
        val modelMapper = ModelMapper()

        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw RuntimeException("인증 정보를 찾을 수 없습니다.")

        if (authentication.name == "anonymousUser") {
            throw RuntimeException("인증되지 않은 사용자입니다.")
        }

        val userEmail = authentication.name
        val user = userService.findByEmail(userEmail)
            ?: throw RuntimeException("사용자를 찾을 수 없습니다.")

        return modelMapper.map(user, UserDto::class.java)
    }

}