package com.project.popupmarket.config.jwt

import com.project.popupmarket.entity.User
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class TokenProvider(
    private val jwtProperties: JwtProperties
) {
    fun generateToken(user: User, expiredAt: Duration): String {
        val now = Date()
        return makeToken(Date(now.time + expiredAt.toMillis()), user)
    }

    private fun makeToken(expiry: Date, user: User): String {
        val now = Date()

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(jwtProperties.issuer)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setSubject(user.email)
            .claim("id", user.id!!.toLong())
            .signWith(
                Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray()),
                SignatureAlgorithm.HS256
            )
            .compact()
    }

    fun validToken(token: String?): Boolean {
        return try {
            if (token == null) return false
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray()))
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val authorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

        return UsernamePasswordAuthenticationToken(
            org.springframework.security.core.userdetails.User(
                claims.subject,
                "",
                authorities
            ),
            token,
            authorities
        )
    }

    fun getUserId(token: String): Long {
        val claims = getClaims(token)
        return claims.get("id", Integer::class.java)?.toLong()
            ?: throw RuntimeException("Invalid token")
    }

    private fun getClaims(token: String) = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray()))
        .build()
        .parseClaimsJws(token)
        .body
}