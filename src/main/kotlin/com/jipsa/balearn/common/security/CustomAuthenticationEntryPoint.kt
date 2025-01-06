package com.jipsa.balearn.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.jipsa.balearn.common.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"

        ApiResponse.error("UNAUTHORIZED", "요청 혹은 인증 정보에 오류가 있습니다.")
            .let {
                val jsonResponse = objectMapper.writeValueAsString(it)
                response.characterEncoding = "UTF-8"
                response.writer.write(jsonResponse)
            }
    }
}