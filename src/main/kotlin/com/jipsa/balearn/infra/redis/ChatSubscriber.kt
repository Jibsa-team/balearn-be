package com.jipsa.balearn.infra.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.jipsa.balearn.api.chat.dto.ChatResponse
import com.jipsa.balearn.domain.chat.Chat
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class ChatSubscriber(
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessagingTemplate
) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val jsonChat = String(message.body, Charsets.UTF_8)

        val chat = objectMapper.readValue(jsonChat, Chat::class.java)

        messagingTemplate.convertAndSend("/sub/api/chat/${chat.teamId.value}", ChatResponse.from(chat))
    }
}