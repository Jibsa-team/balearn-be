package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserId

interface MissionClearRepository {
    fun save(missionClear: MissionClear): MissionClear
    fun save(mission: Mission, teamUser: TeamUser): MissionClear
    fun existsById(missionClearId: MissionClearId): Boolean
    fun existsById(missionId: MissionId, teamUserId: TeamUserId): Boolean
    fun deleteById(missionClearId: MissionClearId)
    fun deleteById(missionId: MissionId, teamUserId: TeamUserId)
    fun deleteByMissionId(missionId: MissionId)
    fun deleteByTeamUserId(teamUserId: TeamUserId)
    fun saveAll(missionClears: List<MissionClear>): List<MissionClear>
    fun deleteAllById(teamUserId: TeamUserId, missionIds: List<MissionId>)
}