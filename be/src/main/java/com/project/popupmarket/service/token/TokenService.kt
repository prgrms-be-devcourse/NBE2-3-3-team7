package com.project.popupmarket.service.token

import com.project.popupmarket.config.jwt.TokenProvider
import com.project.popupmarket.entity.User
import com.project.popupmarket.service.user.UserService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenService(
    private val tokenProvider: TokenProvider,
    private val userService: UserService
) {
    @Cacheable(value = ["refreshToken"], key = "#userId")
    fun findRefreshToken(userId: Long): String? {
        return null
    }

    fun createAccessToken(user: User): String {
        return tokenProvider.generateToken(user, Duration.ofHours(2))
    }

    @CachePut(value = ["refreshToken"], key = "#user.id")
    fun createRefreshToken(user: User): String {
        return tokenProvider.generateToken(user, Duration.ofDays(14))
    }

    fun createNewAccessToken(expiredAccessToken: String): String {
        // 만료된 토큰에서 userId 추출
        val userId = getUserIdFromToken(expiredAccessToken)

        // 해당 유저의 리프레시 토큰이 캐시에 존재하는지 확인
        val refreshToken = findRefreshToken(userId)
            ?: throw IllegalArgumentException("Refresh token not found")

        // 리프레시 토큰이 존재하면 새로운 액세스 토큰 생성
        return try {
            val user = userService.findById(userId)
            createAccessToken(user)
        } catch (e: Exception) {
            throw IllegalArgumentException("User not found")
        }
    }

    @CacheEvict(value = ["refreshToken"], key = "#userId")
    fun deleteRefreshToken(userId: Long) {
        // 캐시에서만 삭제되므로 추가 작업 필요 없음
    }

    // accessToken에서 userId 추출
    fun getUserIdFromToken(token: String): Long {
        return tokenProvider.getUserId(token)
    }
}