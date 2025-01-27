package com.project.popupmarket.service.oauth;

import com.project.popupmarket.dto.oauth.LoginResponse;
import com.project.popupmarket.dto.oauth.OAuthSignupRequest;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.enums.AuthProvider;
import com.project.popupmarket.exception.ErrorCode;
import com.project.popupmarket.exception.oauth2.AuthenticationException;
import com.project.popupmarket.exception.oauth2.SignupException;
import com.project.popupmarket.service.token.TokenService;
import com.project.popupmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private static final long REDIS_TTL_MINUTES = 30;
    private final TokenService tokenService;
    private final UserService userService;
    private final StringRedisTemplate stringTemplate;

    // 구글 게정이 이미 가입되어 있는지 확인
    public LoginResponse handleOAuth2Login(String email) {
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty() || userOptional.get().getSocial() != AuthProvider.GOOGLE) {
            String uuid = saveTemporaryUserData(email);
            return LoginResponse.requireSignup(uuid);
        }

        return createLoginResponse(userOptional.get());
    }

    // 구글 로그인 회원가입 처리
    public LoginResponse handleOAuth2Signup(OAuthSignupRequest request) {
        String email = getEmailFromRedis(request.getUuid());
        if (email == null) {
            throw new AuthenticationException(ErrorCode.UNAUTHORIZED_GOOGLE_REDIS);
        }

        User user = userService.save(request, email)
                               .orElseThrow(() -> new SignupException(ErrorCode.SIGNUP_FAILED));

        stringTemplate.delete(AuthProvider.GOOGLE + request.getUuid());
        return createLoginResponse(user);
    }

    // Redis에 구글 이메일 정보 임시 저장
    private String saveTemporaryUserData(String email) {
        String redisKey = AuthProvider.GOOGLE + email;
        String uuid = UUID
                .randomUUID().toString();
        ValueOperations<String, String> ops = stringTemplate.opsForValue();
        ops.set(redisKey, uuid, Duration.ofMinutes(REDIS_TTL_MINUTES));
        return uuid;
    }

    // Redis에서 구글 이메일 정보 가져오기
    private String getEmailFromRedis(String uuid) {
        ValueOperations<String, String> ops = stringTemplate.opsForValue();
        return ops.get(AuthProvider.GOOGLE + uuid);
    }

    // 로그인 응답 생성
    private LoginResponse createLoginResponse(User user) {
        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = tokenService.createRefreshToken(user);
        return new LoginResponse(accessToken, refreshToken);
    }
}
