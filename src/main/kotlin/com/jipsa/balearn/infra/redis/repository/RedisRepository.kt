package com.jipsa.balearn.infra.redis.repository

import com.jipsa.balearn.domain.user.UserId
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val operationValue = redisTemplate.opsForValue()
    private val operationSet = redisTemplate.opsForSet()
    private val operationZSet = redisTemplate.opsForZSet()

    companion object {
        private const val REFRESH_TOKEN_KEY_PREFIX = "refresh_token:"
        private const val LOGIN_TOKEN_KEY_PREFIX = "login_token:"
        private const val BLACK_LIST_TOKEN_KEY_PREFIX = "black_list_token:"
        private const val TEAM_INVITE_CODE_KEY_PREFIX = "team_invite_code:"
        private const val LEADERBOARD_KEY_PREFIX = "leaderboard:"
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

    fun deleteValue(key: String) {
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

    fun getTopZSet(key: String, topN: Int = 5): Set<ZSetOperations.TypedTuple<String>>? {
        return operationZSet.reverseRangeWithScores(key, 0, (topN - 1).toLong())
    }

    fun generateRefreshTokenKey(userId: UserId): String = "$REFRESH_TOKEN_KEY_PREFIX${userId.value}"
    fun generateLoginTokenKey(userId: UserId): String = "$LOGIN_TOKEN_KEY_PREFIX${userId.value}"
    fun generateBlackListTokenKey(accessToken: String): String = "$BLACK_LIST_TOKEN_KEY_PREFIX${accessToken}"
    fun generateTeamInviteCodeKey(inviteCode: String): String = "$TEAM_INVITE_CODE_KEY_PREFIX${inviteCode}"
    fun generateLeaderboardKey(teamId: Long): String = "$LEADERBOARD_KEY_PREFIX${teamId}"
}