package com.jipsa.balearn.common.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.jipsa.balearn.common.exception.CustomException
import com.jipsa.balearn.common.api.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.text.SimpleDateFormat

@Component
class ExceptionHandlerFilter : OncePerRequestFilter() {
    companion object {
        private const val UNKNOWN_ERROR_CODE = "INTERNAL_SERVER_ERROR"
        private const val UNKNOWN_ERROR_MESSAGE = "알 수 없는 서버 에러입니다"
        private val log = LoggerFactory.getLogger(ExceptionHandlerFilter::class.java)
    }

    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            handleException(request, response, e)
        }
    }

    private fun handleException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: Exception
    ) {
        // 응답이 이미 커밋된 경우 처리하지 않음
        if (response.isCommitted) {
            log.warn("Response is already committed. Skipping error response.")
            return
        }

        log.error("Exception caught in filter: ${exception.message}", exception)

        try {
            response.characterEncoding = "UTF-8"
            response.contentType = "application/json"

            when (exception) {
                is CustomException -> {
                    response.status = exception.status
                    val errorResponse = ApiResponse.error(
                        exception.errorCode.errorReason,
                        request.requestURI,
                        exception.message
                    )
                    response.writer.write(objectMapper.writeValueAsString(errorResponse))
                }

                else -> {
                    response.status = SC_INTERNAL_SERVER_ERROR
                    val errorResponse = ApiResponse.error(
                        UNKNOWN_ERROR_CODE,
                        UNKNOWN_ERROR_MESSAGE
                    )
                    response.writer.write(objectMapper.writeValueAsString(errorResponse))
                }
            }
        } catch (writeException: Exception) {
            log.error("Error while writing error response: ${writeException.message}", writeException)
        }
    }
}
