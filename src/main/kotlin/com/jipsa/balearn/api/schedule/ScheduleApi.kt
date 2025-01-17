package com.jipsa.balearn.api.schedule

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.schedule.dto.ScheduleCreateRequest
import com.jipsa.balearn.api.schedule.dto.ScheduleReadResponse
import com.jipsa.balearn.api.schedule.dto.ScheduleResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.schedule.ScheduleService
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedule")
class ScheduleApi(
    private val scheduleService: ScheduleService
) {

    @PostMapping("/create")
    fun createSchedule(
        @CurrentUser user: User,
        @RequestBody request: ScheduleCreateRequest
    ): ApiResponse<ScheduleReadResponse> {
        return ApiResponse.success(
            ScheduleReadResponse.from(
                scheduleService.appendSchedule(
                    address = request.address,
                    time = request.time,
                    topic = request.topic,
                    missionInfos = request.missions.map { it.toDomain() },
                    user.id,
                    TeamId(request.teamId)
                )
            )
        )
    }
}