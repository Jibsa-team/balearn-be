package com.jipsa.balearn.api.chat

import com.jipsa.balearn.api.chat.dto.ChatListResponse
import com.jipsa.balearn.api.chat.dto.ChatRequest
import com.jipsa.balearn.api.chat.dto.ChatResponse
import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.chat.ChatService
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.exception.CustomUserException
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

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

    @GetMapping("/api/chat/{teamId}")
    fun getChatList(
        @PathVariable teamId: Long,
        @RequestParam size: Int?,
        @RequestParam cursor: LocalDateTime?,
        @CurrentUser user: User
    ): ApiResponse<ChatListResponse> {
        val chatList = chatService.getChatList(user, TeamId(teamId), cursor, size ?: 10)

        return ApiResponse.success(
            ChatListResponse(
                contents = chatList.map { ChatResponse.from(it) },
                cursor = chatList.lastOrNull()?.createdAt
            )
        )
    }
}