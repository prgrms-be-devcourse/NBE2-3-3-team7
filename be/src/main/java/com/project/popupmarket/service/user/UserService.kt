package com.project.popupmarket.service.user

import com.project.popupmarket.dto.auth.LoginRequest
import com.project.popupmarket.dto.oauth.OAuthSignupRequest
import com.project.popupmarket.dto.user.UserRegisterDto
import com.project.popupmarket.dto.user.UserUpdateRequest
import com.project.popupmarket.entity.BusinessId
import com.project.popupmarket.entity.User
import com.project.popupmarket.enums.AuthProvider
import com.project.popupmarket.repository.BusinessIdRepository
import com.project.popupmarket.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.Optional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val businessIdRepository: BusinessIdRepository,
    @Value("\${app.upload-path}")
    private val uploadPath: String
) {
    private val encoder = BCryptPasswordEncoder()
    private val ALLOWED_EXTENSIONS = listOf(".jpg", ".jpeg", ".png", ".gif")
    private val MAX_FILE_SIZE = 2 * 1024 * 1024L // 2MB

    // 이메일로 회원가입 처리
    fun save(dto: UserRegisterDto): Long {
        validateDuplicateEmail(dto.email)

        val user = User(
            email = dto.email,
            password = encoder.encode(dto.password),
            brand = dto.brand,
            name = dto.name,
            tel = dto.tel,
            role = dto.role,
            social = AuthProvider.EMAIL
        )

        val userId = userRepository.save(user).id!!

        businessIdRepository.save(
            BusinessId(
                userId = userId,
                businessId = dto.businessId
            )
        )

        return userId
    }

    // 구글 로그인 회원가입 처리
    fun save(dto: OAuthSignupRequest, email: String): User? {
        val user = User(
            email = email,
            brand = dto.brand,
            name = dto.name,
            tel = dto.tel,
            role = dto.role,
            social = AuthProvider.GOOGLE,
            password = null
        )

        val userId = userRepository.save(user).id!!

        businessIdRepository.save(
            BusinessId(
                userId = userId,
                businessId = dto.businessId
            )
        )

        return userRepository.findUserInfoById(userId)
    }

    fun findById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Unexpected user") }
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    fun updateUser(userId: Long, request: UserUpdateRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // 비밀번호 업데이트
        if (StringUtils.hasText(request.password)) {
            user.setPassword(request.password)
        }

        // 기본 정보 업데이트
        if (StringUtils.hasText(request.name)) user.name = request.name
        if (StringUtils.hasText(request.brand)) user.brand = request.brand
        if (StringUtils.hasText(request.tel)) user.tel = request.tel

        userRepository.save(user)
    }

    // 사용자 삭제
    @Transactional
    fun deleteUser(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }
        userRepository.delete(user)
    }

    // 사용자 인증
    fun authenticate(request: LoginRequest): User {
        // 1. 이메일로 사용자 조회
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("가입되지 않은 이메일입니다.")
        // 2. 비밀번호 검증
        if (!encoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("잘못된 비밀번호입니다.")
        }

        return user
    }

    private fun validateDuplicateEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 가입된 이메일입니다.")
        }
    }
}