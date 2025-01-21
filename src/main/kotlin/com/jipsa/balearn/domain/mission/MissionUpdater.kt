package com.jipsa.balearn.domain.mission

import com.jipsa.balearn.api.mission.dto.MissionUpdateRequest
import com.jipsa.balearn.domain.notice.exception.CustomMissionException
import org.springframework.stereotype.Component

@Component
class MissionUpdater(
    private val missionRepository: MissionRepository
) {
    fun update(mission: Mission, detail: String?): Mission {
        mission.updateMissionInfo(detail)

        return missionRepository.save(mission)
    }

    fun updateMissions(request: List<MissionUpdateRequest>): List<Mission> {
        val missions = request.map {
            val mission = missionRepository.findById(MissionId(it.id))
                ?: throw CustomMissionException.MissionNotFoundException
            mission.updateMissionInfo(it.detail)
            mission
        }
        return missionRepository.saveAll(missions)
    }
}