package com.jipsa.balearn.database.schedule.repository

import com.jipsa.balearn.database.schedule.entity.ScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ScheduleJpaRepository : JpaRepository<ScheduleEntity, Long> {
    fun findByTeam_IdAndScheduleInfo_StartTimeBetween(
        teamId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<ScheduleEntity>

    @Query(
        """
    SELECT COUNT(e) > 0
    FROM ScheduleEntity e
    WHERE e.team.id = :teamId
      AND e.scheduleInfo.startTime < :endTime
      AND e.scheduleInfo.endTime > :startTime
"""
    )
    fun existsByTimeConflict(
        @Param("teamId") teamId: Long,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): Boolean

    fun deleteByTeam_Id(teamId: Long)
}