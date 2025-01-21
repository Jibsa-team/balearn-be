package com.jipsa.balearn.api.schedule.dto

import com.jipsa.balearn.api.mission.dto.MissionUpdateRequest
import java.time.LocalDateTime

data class ScheduleUpdateRequest(
    val address: String?,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val topic: String?,
    val color: String?,
    val missions: List<MissionUpdateRequest>?
) {
}