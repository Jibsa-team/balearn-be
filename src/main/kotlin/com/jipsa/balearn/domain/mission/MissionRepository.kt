package com.jipsa.balearn.domain.mission

import com.jipsa.balearn.domain.schedule.ScheduleId

interface MissionRepository {
    fun save(mission: Mission): Mission
    fun findById(missionId: MissionId): Mission?
    fun findByScheduleId(scheduleId: ScheduleId): List<Mission>
    fun deleteById(missionId: MissionId)
    fun delete(mission: Mission)
}