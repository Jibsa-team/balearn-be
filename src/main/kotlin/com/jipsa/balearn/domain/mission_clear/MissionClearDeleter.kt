package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.schedule.exception.CustomMissionClearException
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class MissionClearDeleter(
    private val missionClearRepository: MissionClearRepository,
    private val redisRepository: RedisRepository
) {
    fun delete(teamUser: TeamUser, missionId: MissionId) {
        if (!missionClearRepository.existsById(missionId, teamUser.id)) {
            throw CustomMissionClearException.AlreadyMissionNotClearException
        }
        missionClearRepository.deleteById(missionId, teamUser.id)
        redisRepository.minusZSetScore(
            key = redisRepository.generateLeaderboardKey(teamUser.id.value),
            value = teamUser.id.value.toString(),
            score = 50.0
        )
    }

    fun deleteAll(teamUser: TeamUser, missionIds: List<MissionId>) {
        missionIds.forEach {
            if (!missionClearRepository.existsById(it, teamUser.id)) {
                throw CustomMissionClearException.AlreadyMissionNotClearException
            }
            redisRepository.minusZSetScore(
                key = redisRepository.generateLeaderboardKey(teamUser.id.value),
                value = teamUser.id.value.toString(),
                score = 50.0
            )
        }
        missionClearRepository.deleteAllById(teamUser.id, missionIds)
    }
}