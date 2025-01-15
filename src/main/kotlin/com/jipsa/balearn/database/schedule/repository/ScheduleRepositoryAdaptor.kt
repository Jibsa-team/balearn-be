package com.jipsa.balearn.database.schedule.repository

import com.jipsa.balearn.database.schedule.entity.ScheduleEntity
import com.jipsa.balearn.domain.schedule.Schedule
import com.jipsa.balearn.domain.schedule.ScheduleId
import com.jipsa.balearn.domain.schedule.ScheduleRepository
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Repository
class ScheduleRepositoryAdaptor(
    private val scheduleJpaRepository: ScheduleJpaRepository
) : ScheduleRepository {
    override fun save(schedule: Schedule): Schedule {
        return scheduleJpaRepository.save(ScheduleEntity.from(schedule)).toDomain()
    }

    override fun findById(scheduleId: ScheduleId): Schedule? {
        return scheduleJpaRepository.findById(scheduleId.value).getOrNull()?.toDomain()
    }

    override fun findByStartDateAndEndDate(
        teamId: TeamId,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Schedule> {
        return scheduleJpaRepository.findByTeam_IdAndScheduleInfo_TimeBetween(teamId.value, startDate, endDate)
            .map { it.toDomain() }
    }

    override fun delete(schedule: Schedule) {
        scheduleJpaRepository.delete(ScheduleEntity.from(schedule))
    }

    override fun deleteById(scheduleId: ScheduleId) {
        scheduleJpaRepository.deleteById(scheduleId.value)
    }
}