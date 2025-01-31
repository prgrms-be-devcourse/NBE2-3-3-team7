package com.project.popupmarket.util

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.util.SerializationUtils
import java.util.*

object CookieUtil {
    // 요청값(이름, 입력, 만료 기간을(초)) 입력 받아 쿠키 생성
    fun addCookie(response: HttpServletResponse, name: String?, value: String?, maxAge: Int) {
        val cookie = Cookie(name, value)
        cookie.path = "/"
        cookie.isHttpOnly = true // Add security setting
        cookie.maxAge = maxAge

        response.addCookie(cookie)
    }

    // 쿠키의 이름을 입력 받아 삭제
    fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
        val cookies = request.cookies ?: return

        for (cookie in cookies) {
            if (name == cookie.name) {
                cookie.value = ""
                cookie.path = "/"
                cookie.maxAge = 0
                cookie.isHttpOnly = true // Add security setting
                response.addCookie(cookie)
            }
        }
    }

    // 쿠키의 이름을 입력 받아 쿠키 반환
    fun getCookie(request: HttpServletRequest, name: String): Optional<Cookie> {
        val cookies = request.cookies

        if (cookies != null) {
            for (cookie in cookies) {
                if (name == cookie.name) {
                    return Optional.of(cookie)
                }
            }
        }

        return Optional.empty()
    }

    // 객체를 직렬화해 쿠키의 값으로 변환
    fun serialize(obj: Any?): String {
        return Base64.getUrlEncoder()
            .encodeToString(SerializationUtils.serialize(obj))
    }

    // 쿠키를 역지렬화해 객체로 변환
    fun <T> deserialize(cookie: Cookie, cls: Class<T>): T {
        return cls.cast(
            SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.value)
            )
        )
    }
}