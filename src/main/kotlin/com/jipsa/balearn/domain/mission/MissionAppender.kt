package com.jipsa.balearn.domain.mission

import org.springframework.stereotype.Component

@Component
class MissionAppender(
    private val missionRepository: MissionRepository
) {
    fun append(mission: Mission): Mission {
        return missionRepository.save(mission)
    }

    fun appendAll(missions: List<Mission>): List<Mission> {
        return missionRepository.saveAll(missions)
    }
}