package com.jipsa.balearn.infra.redis.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.jipsa.balearn.domain.chat.Chat
import com.jipsa.balearn.domain.user.UserId
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val operationValue = redisTemplate.opsForValue()
    private val operationSet = redisTemplate.opsForSet()
    private val operationZSet = redisTemplate.opsForZSet()
    private val operationList = redisTemplate.opsForList()

    companion object {
        private const val REFRESH_TOKEN_KEY_PREFIX = "refresh_token:"
        private const val LOGIN_TOKEN_KEY_PREFIX = "login_token:"
        private const val BLACK_LIST_TOKEN_KEY_PREFIX = "black_list_token:"
        private const val TEAM_INVITE_CODE_KEY_PREFIX = "team_invite_code:"
        private const val LEADERBOARD_KEY_PREFIX = "leaderboard:"
        private const val CHAT_KEY_PREFIX = "chat:team:"
        private const val CHAT_BATCH_KEY_PREFIX = "chat:batch:"
    }

    fun saveValue(key: String, value: String, expirationTime: Long) {
        operationValue.set(key, value, expirationTime, TimeUnit.MILLISECONDS)
    }

    fun getValue(key: String): String? {
        return operationValue[key]
    }

    fun isExistValue(key: String): Boolean {
        return operationValue[key] != null
    }

    fun delete(key: String) {
        redisTemplate.delete(key)
    }

    fun saveSet(key: String, value: String) {
        operationSet.add(key, value)
    }

    fun saveSet(key: String, value: String, expirationTime: Long) {
        operationSet.add(key, value)
        redisTemplate.expire(key, expirationTime, TimeUnit.MILLISECONDS)
    }

    fun isExistSet(key: String, value: String): Boolean {
        return operationSet.isMember(key, value)!!
    }

    fun deleteSet(key: String, value: String) {
        operationSet.remove(key, value)
    }

    fun addZSetScore(key: String, value: String, score: Double) {
        operationZSet.incrementScore(key, value, score)
    }

    fun deleteZSet(key: String, value: String) {
        operationZSet.remove(key, value)
    }

    fun minusZSetScore(key: String, value: String, score: Double) {
        operationZSet.incrementScore(key, value, -score)
    }

    fun getZSetScore(key: String, value: String): Double? {
        return operationZSet.score(key, value)
    }

    fun getZSetRank(key: String, value: String): Long? {
        return operationZSet.reverseRank(key, value)
    }

    fun getTopZSet(key: String, topN: Int = 5): Set<ZSetOperations.TypedTuple<String>>? {
        return operationZSet.reverseRangeWithScores(key, 0, (topN - 1).toLong())
    }

    fun addChatZSetScore(key: String, chat: Chat, score: Double) {
        val jsonChat = objectMapper.writeValueAsString(chat)
        operationZSet.add(key, jsonChat, score)
    }

    fun addChatZSetScores(key: String, valuesWithScores: Map<Chat, Double>) {
        val tupleSet = valuesWithScores.map { (value, score) ->
            ZSetOperations.TypedTuple.of(objectMapper.writeValueAsString(value), score)
        }.toSet()

        operationZSet.add(key, tupleSet)
    }

    fun getChatZSetValue(key: String, startScore: Double, endScore: Double, pageable: Pageable): Set<Chat> {
        operationZSet.reverseRangeByScore(
            key,
            startScore,
            endScore,
            0,
            pageable.pageSize.toLong()
        )?.let { chatSet ->
            return chatSet.map { objectMapper.readValue(it, Chat::class.java) }.toSet()
        } ?: return emptySet()
    }

    fun getChatZSetValue(key: String, startScore: Double, endScore: Double): Set<Chat>? {
        operationZSet.reverseRangeByScore(
            key,
            startScore,
            endScore
        )?.let { chatSet ->
            return chatSet.map { objectMapper.readValue(it, Chat::class.java) }.toSet()
        } ?: return emptySet()
    }

    fun addChatList(key: String, chat: Chat) {
        val jsonChat = objectMapper.writeValueAsString(chat)
        operationList.leftPush(key, jsonChat)
    }

    fun getChatList(key: String, start: Long = 0, end: Long = -1): List<Chat> {
        return operationList.range(key, start, end)?.map { objectMapper.readValue(it, Chat::class.java) } ?: emptyList()
    }

    fun getZSetSize(key: String): Long {
        return operationZSet.size(key) ?: 0
    }

    fun removeRangeZSet(key: String, start: Long, end: Long) {
        operationZSet.removeRange(key, start, end)
    }

    fun scanForKeys(pattern: String): List<String> {
        return redisTemplate.execute { connection ->
            val keys = mutableListOf<String>()
            val options = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000) // 한 번에 최대 1000개 조회
                .build()

            connection.keyCommands().scan(options).use { cursor ->
                while (cursor.hasNext()) {
                    keys.add(String(cursor.next()))
                }
            }

            keys
        } ?: emptyList()
    }

    fun generateRefreshTokenKey(userId: UserId): String = "$REFRESH_TOKEN_KEY_PREFIX${userId.value}"
    fun generateLoginTokenKey(userId: UserId): String = "$LOGIN_TOKEN_KEY_PREFIX${userId.value}"
    fun generateBlackListTokenKey(accessToken: String): String = "$BLACK_LIST_TOKEN_KEY_PREFIX${accessToken}"
    fun generateTeamInviteCodeKey(inviteCode: String): String = "$TEAM_INVITE_CODE_KEY_PREFIX${inviteCode}"
    fun generateLeaderboardKey(teamId: Long): String = "$LEADERBOARD_KEY_PREFIX${teamId}"
    fun generateChatKey(teamId: Long): String = "$CHAT_KEY_PREFIX$teamId"
    fun generateChatBatchKey(): String = CHAT_BATCH_KEY_PREFIX
    fun generateChatKeyPattern(): String = "$CHAT_KEY_PREFIX*"
}