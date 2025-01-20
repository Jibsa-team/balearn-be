package com.jipsa.balearn.domain.schedule

import org.springframework.stereotype.Component

@Component
class ScheduleAppender(
    private val scheduleRepository: ScheduleRepository
) {
    fun append(schedule: Schedule): Schedule {
        schedule.validateScheduleTime()

        return scheduleRepository.save(schedule)
    }
}