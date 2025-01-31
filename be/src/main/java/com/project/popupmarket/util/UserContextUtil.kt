package com.project.popupmarket.util

import com.project.popupmarket.config.jwt.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserContextUtil @Autowired constructor(private val tokenProvider: TokenProvider) {
    val userId: Long?
        get() {
            val authentication =
                SecurityContextHolder.getContext().authentication
            if (authentication != null && authentication.isAuthenticated) {
                val token = authentication.credentials as String
                return tokenProvider.getUserId(token)
            }
            return null
        }
}
