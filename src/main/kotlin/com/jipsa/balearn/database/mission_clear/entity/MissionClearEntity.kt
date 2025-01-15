package com.jipsa.balearn.database.mission_clear.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.domain.mission_clear.MissionClear
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "mission_clears")
class MissionClearEntity(
    @EmbeddedId
    val missionClearId: MissionClearEntityId
) : BaseEntity() {
    fun toDomain() = MissionClear(
        missionClearId = missionClearId.toDomain()
    )

    companion object {
        fun from(missionClear: MissionClear) = MissionClearEntity(
            missionClearId = MissionClearEntityId.from(missionClear.missionClearId)
        )
    }
}