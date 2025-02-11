package com.jipsa.balearn.infra.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.domain.chat.Chat
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class ChatPublisher(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    fun publish(chat: Chat) {
        val jsonChat = objectMapper.writeValueAsString(chat)
        redisTemplate.convertAndSend(BalearnConstants.CHAT_TOPIC, jsonChat)
    }
}