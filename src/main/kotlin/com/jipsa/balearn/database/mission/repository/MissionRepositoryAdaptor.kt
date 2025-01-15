package com.jipsa.balearn.database.mission.repository

import com.jipsa.balearn.database.mission.entity.MissionEntity
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.mission.MissionRepository
import com.jipsa.balearn.domain.schedule.ScheduleId
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class MissionRepositoryAdaptor(
    private val missionJpaRepository: MissionJpaRepository
) : MissionRepository {
    override fun save(mission: Mission): Mission {
        return missionJpaRepository.save(MissionEntity.from(mission)).toDomain()
    }

    override fun findById(missionId: MissionId): Mission? {
        return missionJpaRepository.findById(missionId.value).getOrNull()?.toDomain()
    }

    override fun findByScheduleId(scheduleId: ScheduleId): List<Mission> {
        return missionJpaRepository.findBySchedule_Id(scheduleId.value).map { it.toDomain() }
    }

    override fun deleteById(missionId: MissionId) {
        missionJpaRepository.deleteById(missionId.value)
    }

    override fun delete(mission: Mission) {
        missionJpaRepository.delete(MissionEntity.from(mission))
    }
}