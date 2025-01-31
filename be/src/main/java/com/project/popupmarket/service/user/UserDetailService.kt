package com.project.popupmarket.service.user

import com.project.popupmarket.entity.User
import com.project.popupmarket.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class UserDetailService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String): User =
        userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User not found with email: $email")
}