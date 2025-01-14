package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.team_user.TeamUserId


data class MissionClearId(
    val missionId: MissionId,
    val teamUserId: TeamUserId
)