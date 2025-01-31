package com.project.popupmarket.config.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
        private const val COOKIE_NAME = "jwt_token"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(HEADER_AUTHORIZATION)
        var token = getAccessToken(authorizationHeader)

        // Authorization 헤더에 토큰이 없으면 쿠키에서 토큰 확인
        if (token == null) {
            token = getTokenFromCookie(request)
        }

        // 토큰이 유효하면 인증 처리
        if (token != null && tokenProvider.validToken(token)) {
            val authentication = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun getAccessToken(authorizationHeader: String?): String? {
        return if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            authorizationHeader.substring(TOKEN_PREFIX.length)
        } else null
    }

    private fun getTokenFromCookie(request: HttpServletRequest): String? {
        return request.cookies?.firstOrNull { it.name == COOKIE_NAME }?.value
    }
}
