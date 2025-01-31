package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.schedule.exception.CustomMissionClearException
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class MissionClearAppender(
    private val missionClearRepository: MissionClearRepository,
    private val redisRepository: RedisRepository
) {
    fun append(missionClear: MissionClear): MissionClear {
        if (missionClearRepository.existsById(missionClear.missionClearId)) {
            throw CustomMissionClearException.AlreadyMissionClearException
        }
        redisRepository.addZSetScore(
            key = redisRepository.generateLeaderboardKey(missionClear.teamUser.team.id.value),
            value = missionClear.teamUser.id.value.toString(),
            score = 50.0
        )
        return missionClearRepository.save(missionClear)
    }

    fun appendAll(missionClearList: List<MissionClear>): List<MissionClear> {
        missionClearList.forEach { missionClear ->
            if (missionClearRepository.existsById(missionClear.missionClearId)) {
                throw CustomMissionClearException.AlreadyMissionClearException
            }
            redisRepository.addZSetScore(
                key = redisRepository.generateLeaderboardKey(missionClear.teamUser.team.id.value),
                value = missionClear.teamUser.id.value.toString(),
                score = 50.0
            )
        }
        return missionClearRepository.saveAll(missionClearList)
    }
}