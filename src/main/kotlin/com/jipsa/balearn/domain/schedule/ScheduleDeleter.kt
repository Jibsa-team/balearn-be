package com.jipsa.balearn.domain.schedule

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component

@Component
class ScheduleDeleter(
    private val scheduleRepository: ScheduleRepository
) {
    fun delete(schedule: Schedule) {
        scheduleRepository.delete(schedule)
    }

    fun delete(scheduleId: ScheduleId) {
        scheduleRepository.deleteById(scheduleId)
    }

    fun deleteBy(teamId: TeamId) {
        scheduleRepository.deleteByTeamId(teamId)
    }
}