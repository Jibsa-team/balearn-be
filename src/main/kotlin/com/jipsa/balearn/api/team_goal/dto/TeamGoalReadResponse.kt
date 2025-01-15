package com.jipsa.balearn.api.team_goal.dto

import com.jipsa.balearn.domain.team_goal.TeamGoal
import java.time.LocalDateTime

data class TeamGoalReadResponse(
    val id: Long,
    val detail: String,
    val color: String,
    val createdAt: LocalDateTime?,
    val createdBy: Long?,
    val modifiedAt: LocalDateTime?,
    val modifiedBy: Long?
) {
    companion object {
        fun from(teamGoal: TeamGoal): TeamGoalReadResponse {
            return TeamGoalReadResponse(
                id = teamGoal.id.value,
                detail = teamGoal.teamGoalInfo.detail,
                color = teamGoal.teamGoalInfo.color,
                createdAt = teamGoal.createdAt,
                createdBy = teamGoal.createdBy?.value,
                modifiedAt = teamGoal.modifiedAt,
                modifiedBy = teamGoal.modifiedBy?.value
            )
        }
    }
}