package com.jipsa.balearn.api.schedule.dto

import com.jipsa.balearn.api.mission.dto.MissionReadResponse
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.schedule.Schedule
import java.time.LocalDateTime

class ScheduleReadResponse(
    val id: Long,
    val address: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val topic: String,
    val color: String
) {
    companion object {
        fun from(schedule: Schedule): ScheduleReadResponse {
            return ScheduleReadResponse(
                id = schedule.id.value,
                address = schedule.scheduleInfo.address,
                startTime = schedule.scheduleInfo.startTime,
                endTime = schedule.scheduleInfo.endTime,
                topic = schedule.scheduleInfo.topic,
                color = schedule.scheduleInfo.color
            )
        }
    }
}