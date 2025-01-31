package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.schedule.exception.CustomMissionClearException
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class MissionClearDeleter(
    private val missionClearRepository: MissionClearRepository,
    private val redisRepository: RedisRepository
) {
    fun delete(teamUserId: TeamUserId, missionId: MissionId) {
        if (!missionClearRepository.existsById(missionId, teamUserId)) {
            throw CustomMissionClearException.AlreadyMissionNotClearException
        }
        missionClearRepository.deleteById(missionId, teamUserId)
        redisRepository.minusZSetScore(
            key = redisRepository.generateLeaderboardKey(teamUserId.value),
            value = teamUserId.value.toString(),
            score = 50.0
        )
    }

    fun deleteAll(teamUserId: TeamUserId, missionIds: List<MissionId>) {
        missionIds.forEach {
            if (!missionClearRepository.existsById(it, teamUserId)) {
                throw CustomMissionClearException.AlreadyMissionNotClearException
            }
            redisRepository.minusZSetScore(
                key = redisRepository.generateLeaderboardKey(teamUserId.value),
                value = teamUserId.value.toString(),
                score = 50.0
            )
        }
        missionClearRepository.deleteAllById(teamUserId, missionIds)
    }
}