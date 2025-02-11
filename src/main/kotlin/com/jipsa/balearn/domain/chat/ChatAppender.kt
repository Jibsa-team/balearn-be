package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.common.util.toEpochMillis
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class ChatAppender(
    private val chatRepository: ChatRepository,
    private val redisRepository: RedisRepository
) {
    // TODO: 채팅 저장 구현하기
    fun append(chat: Chat): Chat {
        return chat
    }

    fun appendInRedis(chat: Chat) {
        val chatKey = redisRepository.generateChatKey(chat.teamId.value)
        val newChatKey = redisRepository.generateChatBatchKey()
        redisRepository.addChatZSetScore(chatKey, chat, chat.createdAt!!.toEpochMillis())
        redisRepository.addChatList(newChatKey, chat)
    }

    fun appendInRedisBatch(teamId: TeamId, chatList: List<Chat>) {
        val chatKey = redisRepository.generateChatKey(teamId.value)

        val valueWithScores = chatList.associateBy({ it }, { it.createdAt!!.toEpochMillis() })

        return redisRepository.addChatZSetScores(chatKey, valueWithScores)
    }

    fun appendInElasticsearchBatch(chatList: List<Chat>) {
        chatRepository.saveChatsBulk(chatList)
    }
}