package com.jipsa.balearn.database.mission.repository

import com.jipsa.balearn.database.mission.entity.MissionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MissionJpaRepository : JpaRepository<MissionEntity, Long> {
    fun findBySchedule_Id(scheduleId: Long): List<MissionEntity>
}