package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

class MissionClear(
    val missionClearId: MissionClearId,
    val mission: Mission,
    val teamUser: TeamUser,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    createdBy: UserId? = null,
    modifiedBy: UserId? = null
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    createdBy = createdBy,
    modifiedBy = modifiedBy
) {
}