package com.jipsa.balearn.domain.schedule

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ScheduleUpdater(
    private val scheduleRepository: ScheduleRepository
) {
    fun update(
        schedule: Schedule,
        startTime: LocalDateTime?, endTime: LocalDateTime?, address: String?, topic: String?, color: String?
    ): Schedule {
        schedule.validateScheduleTime()

        schedule.updateScheduleInfo(
            startTime = startTime,
            endTime = endTime,
            address = address,
            topic = topic,
            color = color
        )
        return scheduleRepository.save(schedule)
    }
}