package com.jipsa.balearn.infra.websocket.config

import com.jipsa.balearn.domain.user.CustomUserDetailsService
import com.jipsa.balearn.infra.jwt.JwtProvider
import com.jipsa.balearn.infra.jwt.JwtValidator
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class JwtHandshakeInterceptor(
    private val jwtValidator: JwtValidator,
    private val jwtProvider: JwtProvider,
    private val customUserDetailsService: CustomUserDetailsService
) : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        if (request is ServletServerHttpRequest) {
            val servletRequest = request.servletRequest
            val token = jwtValidator.resolveToken(servletRequest) ?: return false

            jwtValidator.validateToken(token)
            jwtValidator.isLogout(token)
            val userId = jwtProvider.getUserIdFromToken(token)

            val userDetails = customUserDetailsService.loadUserByUsername(userId.value.toString())

            attributes["user"] = userDetails // WebSocket 세션에 사용자 정보 저장
            return true
        }
        return false // 인증 실패 시 WebSocket 연결 차단
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
    }
}