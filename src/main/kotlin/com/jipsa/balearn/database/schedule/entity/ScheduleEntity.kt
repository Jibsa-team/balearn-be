package com.jipsa.balearn.database.schedule.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.domain.schedule.Schedule
import com.jipsa.balearn.domain.schedule.ScheduleId
import jakarta.persistence.*

@Entity
@Table(name = "schedules")
class ScheduleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: TeamEntity,

    @Embedded
    val scheduleInfo: ScheduleInfoVO
) : BaseEntity() {

    fun toDomain(): Schedule {
        return Schedule(
            id = ScheduleId(id),
            team = team.toDomain(),
            _scheduleInfo = scheduleInfo.toDomain()
        )
    }

    companion object {
        fun from(schedule: Schedule): ScheduleEntity {
            return ScheduleEntity(
                id = schedule.id.value,
                team = TeamEntity.from(schedule.team),
                scheduleInfo = ScheduleInfoVO.from(schedule.scheduleInfo)
            )
        }
    }
}