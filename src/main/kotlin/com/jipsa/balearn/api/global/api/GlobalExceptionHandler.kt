package com.jipsa.balearn.api.global.api

import com.jipsa.balearn.common.exception.CustomException
import com.jipsa.balearn.common.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException


@RestControllerAdvice
class GlobalExceptionHandler(
    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
) {
    companion object {
        private const val UNKNOWN_ERROR_CODE = "INTERNAL_SERVER_ERROR"
        private const val UNKNOWN_ERROR_MESSAGE = "알 수 없는 서버 에러입니다"
    }


    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        exception: CustomException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity.status(exception.status)
            .body(
                ApiResponse.error(
                    exception.errorCode.errorReason,
                    request.requestURI,
                    exception.message,
                )
            )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        exception: IllegalArgumentException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.error("IllegalArgumentException: ${exception.message}", exception)
        return ResponseEntity.badRequest()
            .body(
                ApiResponse.error(
                    "BAD_REQUEST",
                    exception.message ?: "Invalid argument provided"
                )
            )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleEntityNotFoundException(
        exception: NoResourceFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.error("EntityNotFoundException: ${exception.message}", exception)
        return ResponseEntity.status(404)
            .body(
                ApiResponse.error(
                    "NOT_FOUND",
                    exception.message ?: "Resource not found"
                )
            )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        exception: AccessDeniedException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.warn("AccessDeniedException: ${exception.message}")
        return ResponseEntity.status(403)
            .body(
                ApiResponse.error(
                    "ACCESS_DENIED",
                    exception.message ?: "Access denied"
                )
            )
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(
        exception: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.error("HttpRequestMethodNotSupportedException: ${exception.message}", exception)
        return ResponseEntity.status(405)
            .body(
                ApiResponse.error(
                    "METHOD_NOT_ALLOWED",
                    exception.message ?: "HTTP method not supported"
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        val errorMessage = exception.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Invalid value" }
        logger.error("ValidationException: $errorMessage", exception)
        return ResponseEntity.badRequest()
            .body(
                ApiResponse.error(
                    "VALIDATION_ERROR",
                    errorMessage
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        exception: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.error("handleException", exception)
        return ResponseEntity.internalServerError()
            .body(
                ApiResponse.error(
                    UNKNOWN_ERROR_CODE,
                    UNKNOWN_ERROR_MESSAGE
                )
            )
    }
}
