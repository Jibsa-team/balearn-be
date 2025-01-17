package com.jipsa.balearn.api.schedule.dto

import com.jipsa.balearn.api.mission.dto.MissionReadResponse
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.schedule.Schedule
import java.time.LocalDateTime

class ScheduleReadResponse(
    val id: Long,
    val address: String,
    val time: LocalDateTime,
    val topic: String,
) {
    companion object {
        fun from(schedule: Schedule): ScheduleReadResponse {
            return ScheduleReadResponse(
                id = schedule.id.value,
                address = schedule.scheduleInfo.address,
                time = schedule.scheduleInfo.time,
                topic = schedule.scheduleInfo.topic,
            )
        }
    }
}