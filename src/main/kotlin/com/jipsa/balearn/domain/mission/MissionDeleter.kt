package com.jipsa.balearn.domain.mission

import org.springframework.stereotype.Component

@Component
class MissionDeleter(
    private val missionRepository: MissionRepository
) {
    fun delete(mission: Mission) {
        missionRepository.delete(mission)
    }

    fun delete(missionId: MissionId) {
        missionRepository.deleteById(missionId)
    }

    fun deleteAllBy(missionIds: List<MissionId>) {
        missionRepository.deleteAllById(missionIds)
    }
}