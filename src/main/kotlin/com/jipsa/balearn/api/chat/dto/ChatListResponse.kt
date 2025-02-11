package com.jipsa.balearn.api.chat.dto

import java.time.LocalDateTime

data class ChatListResponse(
    val contents: List<ChatResponse>,
    val cursor: LocalDateTime?
) {
}