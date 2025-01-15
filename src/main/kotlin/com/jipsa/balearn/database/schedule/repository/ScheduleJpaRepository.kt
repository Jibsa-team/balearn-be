package com.jipsa.balearn.database.schedule.repository

import com.jipsa.balearn.database.schedule.entity.ScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ScheduleJpaRepository : JpaRepository<ScheduleEntity, Long> {
    fun findByTeam_IdAndScheduleInfo_TimeBetween(
        teamId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<ScheduleEntity>
}