package com.jipsa.balearn.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.jipsa.balearn.common.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"

        ApiResponse.error("FORBIDDEN", "권한이 없습니다.")
            .let {
                val jsonResponse = objectMapper.writeValueAsString(it)
                response.characterEncoding = "UTF-8"
                response.writer.write(jsonResponse)
            }
    }

}