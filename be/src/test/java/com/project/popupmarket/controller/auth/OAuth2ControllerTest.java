package com.project.popupmarket.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.popupmarket.dto.oauth.LoginResponse;
import com.project.popupmarket.dto.oauth.OAuthSignupRequest;
import com.project.popupmarket.enums.Role;
import com.project.popupmarket.exception.ErrorCode;
import com.project.popupmarket.exception.oauth2.AuthenticationException;
import com.project.popupmarket.service.oauth.OAuth2Service;
import com.project.popupmarket.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.project.popupmarket.config.jwt.AuthConstants.JWT_TOKEN_COOKIE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OAuth2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OAuth2Service oauth2Service;

    @MockBean
    private UserService userService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations;

    private OAuth2User oauth2User;
    private OAuth2AuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        // OAuth2User 설정
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("name", "Test User");

        oauth2User = new DefaultOAuth2User(Collections.emptyList(), attributes, "email");

        authentication = new OAuth2AuthenticationToken(oauth2User, Collections.emptyList(), "google");

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    @DisplayName("OAuth2 콜백 - 신규 사용자 회원가입 필요")
    void oauth2CallbackNewUserTest() throws Exception {
        // given
        String email = "test@example.com";
        String uuid = "test-uuid";
        given(oauth2Service.handleOAuth2Login(email)).willReturn(LoginResponse.requireSignup(uuid));

        // when & then
        mockMvc.perform(post("/api/auth/oauth2/callback")
                                .with(oauth2Login().oauth2User(oauth2User))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))  // Accept 헤더 추가
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.message").value("회원가입이 필요합니다."))
               .andExpect(jsonPath("$.uuid").value(uuid));
    }

    @Test
    @DisplayName("OAuth2 콜백 - 기존 사용자 로그인 성공")
    void oauth2CallbackExistingUserTest() throws Exception {
        // given
        String email = "test@example.com";
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        given(oauth2Service.handleOAuth2Login(email)).willReturn(new LoginResponse(accessToken, refreshToken));

        // when & then
        mockMvc
                .perform(post("/api/auth/oauth2/callback")
                                 .with(oauth2Login().oauth2User(oauth2User))
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(cookie().exists(JWT_TOKEN_COOKIE_NAME))
                .andExpect(cookie().value(JWT_TOKEN_COOKIE_NAME, refreshToken));
    }

    @Test
    @DisplayName("OAuth2 회원가입 - 성공")
    void oauth2SignupSuccessTest() throws Exception {
        // given
        OAuthSignupRequest request = new OAuthSignupRequest();
        request.setUuid("test-uuid");
        request.setName("Test User");
        request.setBrand("Test Brand");
        request.setTel("010-1234-5678");
        request.setBusinessId("123-45-67890");
        request.setRole(Role.LANDLORD);

        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        given(oauth2Service.handleOAuth2Signup(any(OAuthSignupRequest.class))).willReturn(
                new LoginResponse(accessToken, refreshToken));

        // when & then
        mockMvc
                .perform(post("/api/auth/oauth2/signup")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(cookie().exists(JWT_TOKEN_COOKIE_NAME))
                .andExpect(cookie().value(JWT_TOKEN_COOKIE_NAME, refreshToken));
    }

    @Test
    @DisplayName("OAuth2 회원가입 - 실패 (Redis에서 이메일을 찾을 수 없음)")
    void oauth2SignupFailureTest() throws Exception {
        // given
        OAuthSignupRequest request = new OAuthSignupRequest();
        request.setUuid("invalid-uuid");
        request.setName("Test User");
        request.setBrand("Test Brand");
        request.setTel("010-1234-5678");
        request.setBusinessId("123-45-67890");
        request.setRole(Role.LANDLORD);

        given(oauth2Service.handleOAuth2Signup(any(OAuthSignupRequest.class))).willThrow(
                new AuthenticationException(ErrorCode.UNAUTHORIZED_GOOGLE_REDIS));

        // when & then
        mockMvc
                .perform(post("/api/auth/oauth2/signup")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("구글 계정 인증이 만료되었습니다."));
    }
}