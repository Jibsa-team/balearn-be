package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.common.util.toEpochMillis
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class ChatReader(
    private val chatRepository: ChatRepository,
    private val redisRepository: RedisRepository
) {
    fun readInRedis(teamId: TeamId, cursor: LocalDateTime?, pageable: Pageable): List<Chat> {
        val chatKey = redisRepository.generateChatKey(teamId.value)
        val endScore = cursor?.toEpochMillis()?.minus(0.1) ?: System.currentTimeMillis().toDouble()
        val startScore = Double.NEGATIVE_INFINITY
        return redisRepository.getChatZSetValue(chatKey, startScore, endScore, pageable).toList()
    }

    fun readInRedisBatch(): List<Chat> {
        return redisRepository.getChatList(redisRepository.generateChatBatchKey())
    }

    fun readInElasticsearch(
        teamId: TeamId,
        start: LocalDateTime = LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.MICROS),
        end: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS),
        pageable: Pageable
    ): List<Chat> {
        return chatRepository.findByTeamIdBetween(teamId, start, end, pageable)
    }

    fun readInElasticsearch(teamId: TeamId, cursor: LocalDateTime?, pageable: Pageable): List<Chat> {
        return chatRepository.findByTeamIdAndCursor(
            teamId,
            cursor ?: LocalDateTime.now().truncatedTo(ChronoUnit.MICROS),
            pageable
        )
    }
}