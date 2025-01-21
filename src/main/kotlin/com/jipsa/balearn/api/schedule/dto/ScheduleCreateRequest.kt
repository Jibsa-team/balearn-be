package com.jipsa.balearn.api.schedule.dto

import com.jipsa.balearn.api.mission.dto.MissionCreateRequest
import com.jipsa.balearn.domain.team.TeamId
import java.time.LocalDateTime

data class ScheduleCreateRequest(
    val teamId: Long,
    val address: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val topic: String,
    val color: String?,
    val missions: List<MissionCreateRequest>
) {
}