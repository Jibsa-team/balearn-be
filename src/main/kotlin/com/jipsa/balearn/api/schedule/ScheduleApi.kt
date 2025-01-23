package com.jipsa.balearn.api.schedule

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.schedule.dto.*
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.mission.MissionId
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
                    userId = user.id,
                    color = request.color,
                    teamId = TeamId(request.teamId)
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

    @PutMapping("/{scheduleId}")
    fun updateSchedule(
        @CurrentUser user: User,
        @PathVariable scheduleId: Long,
        @RequestBody request: ScheduleUpdateRequest
    ): ApiResponse<ScheduleResponse> {
        return ApiResponse.success(
            scheduleService.updateSchedule(
                ScheduleId(scheduleId),
                address = request.address,
                startTime = request.startTime,
                endTime = request.endTime,
                topic = request.topic,
                missionUpdateRequest = request.missions,
                user = user,
                color = request.color,
                deleteMissionIds = request.deleteMissions?.map { MissionId(it) }
            )
        )
    }

    @DeleteMapping("/{scheduleId}")
    fun deleteSchedule(
        @CurrentUser user: User,
        @PathVariable scheduleId: Long
    ): ApiResponse<Unit> {
        scheduleService.deleteSchedule(ScheduleId(scheduleId), user)
        return ApiResponse.success()
    }
}