package com.jipsa.balearn.domain.mission

import com.jipsa.balearn.domain.notice.exception.CustomMissionException
import com.jipsa.balearn.domain.schedule.ScheduleId
import org.springframework.stereotype.Component

@Component
class MissionReader(
    private val missionRepository: MissionRepository
) {
    fun read(missionId: MissionId): Mission {
        return missionRepository.findById(missionId) ?: throw CustomMissionException.MissionNotFoundException
    }

    fun readBy(scheduleId: ScheduleId): List<Mission> {
        return missionRepository.findByScheduleId(scheduleId)
    }
}