package com.jipsa.balearn.api.chat.dto

import com.jipsa.balearn.domain.chat.Chat
import com.jipsa.balearn.domain.chat.ChatType
import com.jipsa.balearn.domain.chat.Sender
import java.time.LocalDateTime

data class ChatResponse(
    val id: Long,
    val teamId: Long,
    val sender: Sender,
    val message: String,
    val type: ChatType,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?
) {
    companion object {
        fun from(chat: Chat): ChatResponse {
            return ChatResponse(
                id = chat.id.value,
                teamId = chat.teamId.value,
                sender = chat.sender,
                message = chat.chatInfo.message,
                type = chat.chatInfo.type,
                createdAt = chat.createdAt,
                modifiedAt = chat.modifiedAt
            )
        }
    }
}