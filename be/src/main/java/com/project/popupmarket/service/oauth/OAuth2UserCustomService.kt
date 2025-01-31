package com.project.popupmarket.service.oauth

import com.project.popupmarket.repository.BusinessIdRepository
import com.project.popupmarket.repository.UserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class OAuth2UserCustomService(
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        println("OAuth2 User Attributes: ${oAuth2User.attributes}")

        return oAuth2User
    }
}