package com.project.popupmarket.config

import com.project.popupmarket.config.jwt.TokenAuthenticationFilter
import com.project.popupmarket.config.jwt.TokenProvider
import com.project.popupmarket.repository.OAuth2AuthorizationRequestBasedOnCookieRepository
import com.project.popupmarket.service.oauth.OAuth2UserCustomService
import com.project.popupmarket.service.user.UserDetailService
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class WebOAuthSecurityConfig(
    private val oAuth2UserCustomService: OAuth2UserCustomService,
    private val tokenProvider: TokenProvider,
    private val userDetailService: UserDetailService
) {

    // WebSecurityCustomizer 빈 등록
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web
                .ignoring()
                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers("/static/**")
        }
    }

    // SecurityFilterChain 빈 등록
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .httpBasic { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }
            .sessionManagement { session ->
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 필요시 생성
        }

        // JWT 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        // API 요청에 대한 권한 설정
        http.authorizeHttpRequests { authorizeRequests ->
            authorizeRequests
                .requestMatchers("/api/signup", "/api/login")
                .permitAll()
                .requestMatchers("/api/**")
                .permitAll() // 추후 수정
                // 누구나 접근 가능한 페이지
                .requestMatchers(AntPathRequestMatcher.antMatcher("/"))
                .permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/main"))
                .permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/popup/list"))
                .permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/rental/list"))
                .permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/rental/detail/**"))
                .permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/popup/detail/**"))
                .permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/register/**"))
                .permitAll()
                .requestMatchers("/oauth2/**")
                .permitAll()
                .requestMatchers("/login/oauth2/**")
                .permitAll() // 로그인 필요한 페이지

                .requestMatchers(AntPathRequestMatcher.antMatcher("/mypage/**"))
                .authenticated()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/payment"))
                .authenticated()

                .anyRequest()
                .permitAll()
        }

        // 폼 로그인 설정
        //        http.formLogin(formLogin -> formLogin
        //                .loginPage("/login")
        //                .usernameParameter("email")
        //                .passwordParameter("password")
        //                .loginProcessingUrl("/login")
        //                .successHandler(formLoginSuccessHandler())
        //                .permitAll()
        //        );

        // OAuth2 로그인 설정
        http.oauth2Login { oauth2 ->
            oauth2
                .loginPage("/login")
                .authorizationEndpoint { authorization ->
                    authorization.authorizationRequestRepository(
                        oAuth2AuthorizationRequestBasedOnCookieRepository()
                    )
                }
                .defaultSuccessUrl("/api/auth/oauth2/callback")
                .userInfoEndpoint { userInfo ->
                    userInfo.userService(oAuth2UserCustomService)
                }
        }

        http.exceptionHandling { exception: ExceptionHandlingConfigurer<HttpSecurity?> ->
            exception.defaultAuthenticationEntryPointFor(
                HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                AntPathRequestMatcher("/api/**")
            )
        }

        return http.build()
    }

    // OAuth2 로그인 성공 핸들러 추가
    //    @Bean
    //    public OAuth2SuccessHandler oAuth2SuccessHandler() {
    //        return new OAuth2SuccessHandler(tokenProvider, jwtTokenRepository,
    //                                        oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    //    }
    // 폼 로그인 성공 핸들러 추가
    //    @Bean
    //    public FormLoginSuccessHandler formLoginSuccessHandler() {
    //        return new FormLoginSuccessHandler(tokenProvider, jwtTokenRepository);
    //    }
    // TokenAuthenticationFilter 빈 등록
    @Bean
    fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
        return TokenAuthenticationFilter(tokenProvider!!)
    }

    // OAuth2AuthorizationRequestBasedOnCookieRepository 빈 등록
    @Bean
    fun oAuth2AuthorizationRequestBasedOnCookieRepository(): OAuth2AuthorizationRequestBasedOnCookieRepository {
        return OAuth2AuthorizationRequestBasedOnCookieRepository()
    }

    // AuthenticationManager 빈 등록
    @Bean
    @Throws(Exception::class)
    fun authenticationManager(
        http: HttpSecurity,
        bCryptPasswordEncoder: BCryptPasswordEncoder
    ): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder
            .userDetailsService(userDetailService)
            .passwordEncoder(bCryptPasswordEncoder)
        return authenticationManagerBuilder.build()
    }

    // BCryptPasswordEncoder 빈 등록
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}