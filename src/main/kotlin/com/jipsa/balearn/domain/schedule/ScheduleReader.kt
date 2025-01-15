package com.jipsa.balearn.domain.schedule

import com.jipsa.balearn.domain.schedule.exception.CustomScheduleException
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

@Component
class ScheduleReader(
    private val scheduleRepository: ScheduleRepository
) {
    fun read(scheduleId: ScheduleId): Schedule {
        return scheduleRepository.findById(scheduleId)
            ?: throw CustomScheduleException.ScheduleNotFoundException
    }

    fun readWeeklyScheduleBy(teamId: TeamId): List<Schedule> {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay()
        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX)
        return scheduleRepository.findByStartDateAndEndDate(teamId, startOfWeek, endOfWeek)
    }

    fun readMonthlyScheduleBy(teamId: TeamId): List<Schedule> {
        val today = LocalDate.now()
        val startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay()
        val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX)
        return scheduleRepository.findByStartDateAndEndDate(teamId, startOfMonth, endOfMonth)
    }
}