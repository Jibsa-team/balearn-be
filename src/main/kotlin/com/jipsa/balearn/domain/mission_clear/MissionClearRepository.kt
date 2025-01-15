package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.team_user.TeamUserId

interface MissionClearRepository {
    fun save(missionClear: MissionClear): MissionClear
    fun save(missionId: MissionId, teamUserId: TeamUserId): MissionClear
    fun existsById(missionClearId: MissionClearId): Boolean
    fun existsById(missionId: MissionId, teamUserId: TeamUserId): Boolean
    fun deleteById(missionClearId: MissionClearId)
    fun deleteById(missionId: MissionId, teamUserId: TeamUserId)
}