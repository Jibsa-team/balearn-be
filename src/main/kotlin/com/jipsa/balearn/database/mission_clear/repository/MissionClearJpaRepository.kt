package com.jipsa.balearn.database.mission_clear.repository

import com.jipsa.balearn.database.mission_clear.entity.MissionClearEntity
import com.jipsa.balearn.database.mission_clear.entity.MissionClearEntityId
import org.springframework.data.jpa.repository.JpaRepository

interface MissionClearJpaRepository : JpaRepository<MissionClearEntity, MissionClearEntityId> {
    fun existsMissionClearEntityByMissionClearId_MissionIdAndMissionClearId_TeamUserId(
        missionId: Long,
        teamUserId: Long
    ): Boolean

    fun deleteByMissionClearId_MissionId(missionId: Long)

    fun deleteByMissionClearId_TeamUserId(teamUserId: Long)
}