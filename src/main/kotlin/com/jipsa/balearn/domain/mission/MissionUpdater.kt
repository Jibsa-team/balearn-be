package com.jipsa.balearn.domain.mission

import com.jipsa.balearn.api.mission.dto.MissionUpdateRequest
import com.jipsa.balearn.domain.notice.exception.CustomMissionException
import com.jipsa.balearn.domain.schedule.Schedule
import org.springframework.stereotype.Component

@Component
class MissionUpdater(
    private val missionRepository: MissionRepository
) {
    fun update(mission: Mission, detail: String?): Mission {
        mission.updateMissionInfo(detail)

        return missionRepository.save(mission)
    }

    fun updateMissions(requests: List<MissionUpdateRequest>, schedule: Schedule): List<Mission> {
        val missions = requests.map { request ->
            request.id?.let {
                val mission = missionRepository.findById(MissionId(it))
                    ?: throw CustomMissionException.MissionNotFoundException
                mission.updateMissionInfo(request.detail)
                mission
            } ?: Mission(
                _missionInfo = MissionInfo(
                    detail = request.detail,
                ),
                schedule = schedule
            )
        }
        return missionRepository.saveAll(missions)
    }
}