package com.jipsa.balearn.infra.redis

import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.domain.chat.Chat
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class ChatPublisher(
    @Qualifier("anyRedisTemplate")
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    fun publish(chat: Chat) {
        redisTemplate.convertAndSend(BalearnConstants.CHAT_TOPIC, chat)
    }
}