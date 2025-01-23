package com.jipsa.balearn.database.mission_clear.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.database.mission.entity.MissionEntity
import com.jipsa.balearn.database.team_user.entity.TeamUserEntity
import com.jipsa.balearn.domain.mission_clear.MissionClear
import jakarta.persistence.*

@Entity
@Table(name = "mission_clears")
class MissionClearEntity(
    @EmbeddedId
    val missionClearId: MissionClearEntityId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false, insertable = false, updatable = false)
    val mission: MissionEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_user_id", nullable = false, insertable = false, updatable = false)
    val teamUser: TeamUserEntity

) : BaseEntity() {
    fun toDomain() = MissionClear(
        missionClearId = missionClearId.toDomain(),
        mission = mission.toDomain(),
        teamUser = teamUser.toDomain()
    )

    companion object {
        fun from(missionClear: MissionClear) = MissionClearEntity(
            missionClearId = MissionClearEntityId.from(missionClear.missionClearId),
            mission = MissionEntity.from(missionClear.mission),
            teamUser = TeamUserEntity.from(missionClear.teamUser)
        )
    }
}