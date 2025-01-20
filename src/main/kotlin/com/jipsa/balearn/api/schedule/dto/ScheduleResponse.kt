package com.jipsa.balearn.api.schedule.dto

import com.jipsa.balearn.api.mission.dto.MissionReadResponse
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.schedule.Schedule
import java.time.LocalDateTime

data class ScheduleResponse(
    val id: Long,
    val address: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val topic: String,
    val mission: List<MissionReadResponse>
) {
    companion object {
        fun from(schedule: Schedule, missions: List<Mission>): ScheduleResponse {
            return ScheduleResponse(
                id = schedule.id.value,
                address = schedule.scheduleInfo.address,
                startTime = schedule.scheduleInfo.startTime,
                endTime = schedule.scheduleInfo.endTime,
                topic = schedule.scheduleInfo.topic,
                mission = missions.map { MissionReadResponse.from(it) }
            )
        }
    }
}