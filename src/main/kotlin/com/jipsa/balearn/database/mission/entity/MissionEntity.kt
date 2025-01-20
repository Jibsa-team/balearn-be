package com.jipsa.balearn.database.mission.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.database.schedule.entity.ScheduleEntity
import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.mission.MissionInfo
import com.jipsa.balearn.domain.schedule.Schedule
import com.jipsa.balearn.domain.user.UserId
import jakarta.persistence.*

@Entity
@Table(name = "missions")
class MissionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    val schedule: ScheduleEntity,

    @Embedded
    val missionInfo: MissionInfoVO
) : BaseEntity() {
    fun toDomain(): Mission {
        return Mission(
            id = MissionId(id),
            schedule = schedule.toDomain(),
            _missionInfo = missionInfo.toDomain(),
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            createdBy = createdBy?.let { UserId(it) },
            modifiedBy = modifiedBy?.let { UserId(it) }
        )
    }

    companion object {
        fun from(mission: Mission): MissionEntity {
            return MissionEntity(
                id = mission.id.value,
                schedule = ScheduleEntity.from(mission.schedule),
                missionInfo = MissionInfoVO.from(mission.missionInfo)
            )
        }
    }
}