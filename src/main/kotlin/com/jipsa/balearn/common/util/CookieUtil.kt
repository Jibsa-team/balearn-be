package com.jipsa.balearn.common.util

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class CookieUtil(
    private val environment: Environment,
    @Value("\${frontend.domain}") private val frontendDomain: String
) {

    companion object {
        private const val DEFAULT_COOKIE_MAX_AGE = 7 * 24 * 60 * 60 // 7일 (초 단위)
        private const val DEFAULT_PATH = "/"
    }

    /**
     * 활성화된 프로파일에 따라 쿠키의 secure 여부를 결정
     */
    private fun isSecure(): Boolean {
        val activeProfiles = environment.activeProfiles
        return !activeProfiles.contains("local")
    }

    /**
     * 쿠키를 생성하고 응답에 추가
     * @param response 쿠키를 추가할 HttpServletResponse
     * @param name 쿠키의 이름
     * @param value 쿠키의 값
     * @param maxAge 쿠키의 유효 기간(초 단위, 기본값: 7일)
     */
    fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int = DEFAULT_COOKIE_MAX_AGE) {
        val cookie = Cookie(name, value).apply {
            isHttpOnly = true
            path = DEFAULT_PATH
            this.maxAge = maxAge
            secure = isSecure()
            if (secure) {
                setAttribute("SameSite", "None")
//                domain = frontendDomain
            }
        }
        response.addCookie(cookie)
    }

    /**
     * 이름으로 쿠키 값 가져오기
     * @param request 쿠키를 검색할 HttpServletRequest
     * @param name 가져올 쿠키의 이름
     * @return 쿠키의 값 또는 null (존재하지 않을 경우)
     */
    fun getCookieValue(request: HttpServletRequest, name: String): String? {
        val cookies = request.cookies ?: return null
        return cookies.firstOrNull { it.name == name }?.value
    }

    /**
     * 쿠키 삭제 (maxAge를 0으로 설정)
     * @param response 수정할 HttpServletResponse
     * @param name 삭제할 쿠키의 이름
     */
    fun deleteCookie(response: HttpServletResponse, name: String) {
        val cookie = Cookie(name, "").apply {
            isHttpOnly = true
            path = DEFAULT_PATH
            maxAge = 0
            secure = isSecure()
            if (secure) {
                setAttribute("SameSite", "None")
//                domain = frontendDomain
            }
        }
        response.addCookie(cookie)
    }
}
