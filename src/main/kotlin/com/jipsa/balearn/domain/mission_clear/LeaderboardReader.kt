package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.api.mission_clear.dto.LeaderboardResponse
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.domain.team_user.TeamUserReader
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class LeaderboardReader(
    private val redisRepository: RedisRepository,
    private val teamUserReader: TeamUserReader
) {
    fun read(teamId: TeamId, topN: Int = 5): List<LeaderboardResponse> {
        val results = redisRepository.getTopZSet(redisRepository.generateLeaderboardKey(teamId.value), topN)
        return results?.mapIndexed { index, tuple ->
            val teamUserId = tuple.value?.let { TeamUserId(it.toLong()) } // Redis에서 가져온 userId
            val score = tuple.score?.toInt() ?: 0 // 점수


            val teamUser = teamUserId?.let {
                try {
                    teamUserReader.read(it)
                } catch (e: Exception) {
                    null
                }
            }

            teamUser?.let {
                LeaderboardResponse.from(
                    rank = index + 1,
                    teamUser = it,
                    score = score
                )
            } ?: LeaderboardResponse(
                rank = index + 1,
                nickname = "탈퇴한 유저",
                profileImgUrl = "https://cdn.balearn.o-r.kr/profile/default-user.jpg",
                score = score
            )
        } ?: emptyList()
    }

    fun read(teamUser: TeamUser): LeaderboardResponse {
        val score = redisRepository.getZSetScore(
            redisRepository.generateLeaderboardKey(teamUser.team.id.value),
            teamUser.user.id.value.toString()
        ) ?: 0.0
        val rank = redisRepository.getZSetRank(
            redisRepository.generateLeaderboardKey(teamUser.team.id.value),
            teamUser.user.id.value.toString()
        ) ?: 0
        return LeaderboardResponse.from(
            rank = rank.toInt() + 1,
            teamUser = teamUser,
            score = score.toInt()
        )
    }
}