package com.jipsa.balearn.api.schedule

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.schedule.dto.ScheduleCreateRequest
import com.jipsa.balearn.api.schedule.dto.ScheduleReadParam
import com.jipsa.balearn.api.schedule.dto.ScheduleReadResponse
import com.jipsa.balearn.api.schedule.dto.ScheduleResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.schedule.ScheduleId
import com.jipsa.balearn.domain.schedule.ScheduleService
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.*

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
                    startTime = request.startTime,
                    endTime = request.endTime,
                    topic = request.topic,
                    missionInfos = request.missions.map { it.toDomain() },
                    user.id,
                    TeamId(request.teamId)
                )
            )
        )
    }

    @GetMapping("/{scheduleId}")
    fun getSchedule(
        @CurrentUser user: User,
        @PathVariable scheduleId: Long
    ): ApiResponse<ScheduleResponse> {
        return ApiResponse.success(
            scheduleService.readSchedule(ScheduleId(scheduleId), user.id)
        )
    }

    @GetMapping("/month/team/{teamId}")
    fun getScheduleByMonth(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
        scheduleReadParam: ScheduleReadParam
    ): ApiResponse<List<ScheduleReadResponse>> {
        val year = scheduleReadParam.year ?: return ApiResponse.success(
            scheduleService.readMonthlySchedules(user.id, TeamId(teamId))
                .map { ScheduleReadResponse.from(it) }
        )

        val month = scheduleReadParam.month ?: 1

        return ApiResponse.success(
            scheduleService.readMonthlySchedules(year, month, user.id, TeamId(teamId))
                .map { ScheduleReadResponse.from(it) }
        )
    }

    @GetMapping("/week/team/{teamId}")
    fun getScheduleByWeek(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
        scheduleReadParam: ScheduleReadParam
    ): ApiResponse<List<ScheduleReadResponse>> {
        val year = scheduleReadParam.year ?: return ApiResponse.success(
            scheduleService.readWeeklySchedules(user.id, TeamId(teamId))
                .map { ScheduleReadResponse.from(it) }
        )

        val week = scheduleReadParam.week ?: 1

        return ApiResponse.success(
            scheduleService.readWeeklySchedules(year, week, user.id, TeamId(teamId))
                .map { ScheduleReadResponse.from(it) }
        )
    }
}