package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class ChatDeleter(
    private val chatRepository: ChatRepository,
    private val redisRepository: RedisRepository
) {
    fun deleteChatInRedisBatch() {
        redisRepository.delete(redisRepository.generateChatBatchKey())
    }

    fun decreaseChatInRedis() {
        val maxMessagesPerTeam = 1000

        val teamKeys = redisRepository.scanForKeys(redisRepository.generateChatKeyPattern())

        teamKeys.forEach { teamKey ->
            val currentSize = redisRepository.getZSetSize(teamKey)

            if (currentSize > maxMessagesPerTeam) {
                val excessCount = currentSize - maxMessagesPerTeam
                redisRepository.removeRangeZSet(teamKey, 0, excessCount - 1) // 오래된 메시지 삭제
            }
        }
    }
}