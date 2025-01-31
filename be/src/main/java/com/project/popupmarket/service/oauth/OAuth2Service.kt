package com.project.popupmarket.service.oauth

import com.project.popupmarket.dto.oauth.LoginResponse
import com.project.popupmarket.dto.oauth.OAuthSignupRequest
import com.project.popupmarket.entity.User
import com.project.popupmarket.enums.AuthProvider
import com.project.popupmarket.exception.ErrorCode
import com.project.popupmarket.exception.oauth2.AuthenticationException
import com.project.popupmarket.exception.oauth2.SignupException
import com.project.popupmarket.service.token.TokenService
import com.project.popupmarket.service.user.UserService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class OAuth2Service(
    private val tokenService: TokenService,
    private val userService: UserService,
    private val stringTemplate: StringRedisTemplate
) {
    companion object {
        private const val REDIS_TTL_MINUTES = 30L
    }

    // 구글 계정이 이미 가입되어 있는지 확인
    fun handleOAuth2Login(email: String): LoginResponse {
        val user = userService.findByEmail(email)

        if (user == null || user.social != AuthProvider.GOOGLE) {
            val uuid = saveTemporaryUserData(email)
            return LoginResponse.requireSignup(uuid)
        }

        return createLoginResponse(user)
    }

    // 구글 로그인 회원가입 처리
    fun handleOAuth2Signup(request: OAuthSignupRequest): LoginResponse {
        val email = getEmailFromRedis(request.uuid)
            ?: throw AuthenticationException(ErrorCode.UNAUTHORIZED_GOOGLE_REDIS)

        val user = userService.save(request, email)
            ?: throw SignupException(ErrorCode.SIGNUP_FAILED)

        stringTemplate.delete("${AuthProvider.GOOGLE}${request.uuid}")
        return createLoginResponse(user)
    }

    // Redis에 구글 이메일 정보 임시 저장
    private fun saveTemporaryUserData(email: String): String {
        val redisKey = "${AuthProvider.GOOGLE}${email}"
        val uuid = UUID.randomUUID().toString()
        val ops = stringTemplate.opsForValue()
        ops.set(redisKey, uuid, Duration.ofMinutes(REDIS_TTL_MINUTES))
        return uuid
    }

    // Redis에서 구글 이메일 정보 가져오기
    private fun getEmailFromRedis(uuid: String): String? {
        val ops = stringTemplate.opsForValue()
        return ops.get("${AuthProvider.GOOGLE}${uuid}")
    }

    // 로그인 응답 생성
    private fun createLoginResponse(user: User): LoginResponse {
        val accessToken = tokenService.createAccessToken(user)
        val refreshToken = tokenService.createRefreshToken(user)
        return LoginResponse(accessToken, refreshToken)
    }
}