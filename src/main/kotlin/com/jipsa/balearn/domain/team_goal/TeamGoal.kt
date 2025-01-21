package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

class TeamGoal(
    val id: TeamGoalId = TeamGoalId(),
    val team: Team,
    private var _teamGoalInfo: TeamGoalInfo,
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
    val teamGoalInfo: TeamGoalInfo
        get() = _teamGoalInfo

    fun updateInfo(detail: String?, color: String?) {
        this._teamGoalInfo = TeamGoalInfo(
            detail = detail ?: teamGoalInfo.detail,
            color = color ?: teamGoalInfo.color
        )
    }
}