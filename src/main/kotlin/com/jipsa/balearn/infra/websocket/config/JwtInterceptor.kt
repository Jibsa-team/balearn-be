package com.jipsa.balearn.infra.websocket.config

import com.jipsa.balearn.domain.user.UserReader
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.infra.jwt.JwtProvider
import com.jipsa.balearn.infra.jwt.JwtValidator
import com.jipsa.balearn.infra.jwt.exception.CustomJwtException
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

@Component
class JwtInterceptor(
    private val jwtValidator: JwtValidator,
    private val jwtProvider: JwtProvider,
    private val userReader: UserReader,
    private val teamUserValidator: TeamUserValidator
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        // WebSocket 연결(첫 번째 핸드셰이크) 시 JWT 검증 수행
        if (StompCommand.CONNECT == accessor.command) {
            val token = accessor.getFirstNativeHeader("Authorization")?.let { jwtValidator.resolveToken(it) }

            if (token.isNullOrBlank()) {
                throw CustomJwtException.JwtNotFountException
            }

            // JWT 검증
            jwtValidator.validateToken(token)
            jwtValidator.isLogout(token)
            val userId = jwtProvider.getUserIdFromToken(token)
            val user = userReader.read(userId)

            // 사용자 정보를 WebSocket 세션에 저장
            accessor.sessionAttributes?.set("user", user)
        } else if (accessor.command == StompCommand.SUBSCRIBE || accessor.command == StompCommand.SEND) {
            val user = accessor.sessionAttributes?.get("user") as? User
                ?: throw CustomJwtException.JwtNotFountException
            val pattern = if (accessor.command == StompCommand.SUBSCRIBE) subscription else publication
            val teamId = accessor.destination?.let(pattern::matchEntire)?.groupValues?.get(1)?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid chat destination")
            teamUserValidator.validTeamUser(TeamId(teamId), user.id)
        }

        return message
    }

    private companion object {
        val subscription = Regex("/sub/api/chat/([0-9]+)")
        val publication = Regex("/pub/api/chat/send/([0-9]+)")
    }
}
