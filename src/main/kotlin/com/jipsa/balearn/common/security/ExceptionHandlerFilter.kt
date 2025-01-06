package com.jipsa.balearn.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.grepp.quizy.common.exception.CustomException
import com.jipsa.balearn.api.global.api.GlobalExceptionHandler
import com.jipsa.balearn.api.global.api.GlobalExceptionHandler.Companion
import com.jipsa.balearn.common.api.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.text.SimpleDateFormat

@Component
class ExceptionHandlerFilter : OncePerRequestFilter() {
    companion object {
        private const val UNKNOWN_ERROR_CODE = "INTERNAL_SERVER_ERROR"
        private const val UNKNOWN_ERROR_MESSAGE = "알 수 없는 서버 에러입니다"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))

        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {

            when (e) {
                is CustomException -> {
                    response.status = e.status
                    response.contentType = "application/json"
                    ApiResponse.error(
                        e.errorCode.errorReason,
                        request.requestURI,
                        e.message,
                    ).let {
                        val jsonResponse = objectMapper.writeValueAsString(it)
                        response.characterEncoding = "UTF-8"
                        response.writer.write(jsonResponse)
                    }
                }

                else -> {
                    response.status = SC_INTERNAL_SERVER_ERROR
                    response.contentType = "application/json"
                    ApiResponse.error(
                        UNKNOWN_ERROR_CODE,
                        e.message
                            ?: UNKNOWN_ERROR_MESSAGE,
                    ).let {
                        val jsonResponse = objectMapper.writeValueAsString(it)
                        response.characterEncoding = "UTF-8"
                        response.writer.write(jsonResponse)
                    }
                }
            }
        }
    }
}