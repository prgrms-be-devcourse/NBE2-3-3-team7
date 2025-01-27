package com.project.popupmarket.service.oauth;

import com.project.popupmarket.dto.oauth.OAuthSignupRequest;
import com.project.popupmarket.dto.user.UserRegisterDto;
import com.project.popupmarket.entity.BusinessId;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.enums.AuthProvider;
import com.project.popupmarket.repository.BusinessIdRepository;
import com.project.popupmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    @Autowired
    private StringRedisTemplate stringTemplate;
    private final UserRepository userRepository;
    private final BusinessIdRepository businessIdRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return super.loadUser(userRequest);
    }



}