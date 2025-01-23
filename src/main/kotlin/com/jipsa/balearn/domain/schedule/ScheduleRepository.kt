package com.jipsa.balearn.domain.schedule

import com.jipsa.balearn.domain.team.TeamId
import java.time.LocalDateTime

interface ScheduleRepository {
    fun save(schedule: Schedule): Schedule
    fun findById(scheduleId: ScheduleId): Schedule?
    fun findByStartDateAndEndDate(teamId: TeamId, startDate: LocalDateTime, endDate: LocalDateTime): List<Schedule>
    fun delete(schedule: Schedule)
    fun deleteById(scheduleId: ScheduleId)
    fun existByStartDateAndEndDate(teamId: TeamId, startDate: LocalDateTime, endDate: LocalDateTime): Boolean
    fun deleteByTeamId(teamId: TeamId)
}