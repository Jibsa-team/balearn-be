package com.jipsa.balearn.api.chat

import com.jipsa.balearn.api.chat.dto.ChatRequest
import com.jipsa.balearn.domain.chat.ChatService
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.exception.CustomUserException
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatApi(
    private val chatService: ChatService
) {
    @MessageMapping("/api/chat/send/{teamId}")
    fun sendChatMessage(
        @Payload request: ChatRequest,
        @DestinationVariable teamId: Long,
        headerAccessor: StompHeaderAccessor
    ) {
        val sessionAttributes = headerAccessor.sessionAttributes
        val user = sessionAttributes?.get("user") as? User
            ?: throw CustomUserException.UserNotAuthenticatedException

        chatService.sendChat(TeamId(teamId), request.message, user)
    }
}