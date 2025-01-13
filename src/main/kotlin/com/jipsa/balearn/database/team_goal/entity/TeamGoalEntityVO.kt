package com.jipsa.balearn.database.team_goal.entity

import com.jipsa.balearn.domain.team_goal.TeamGoal
import com.jipsa.balearn.domain.team_goal.TeamGoalInfo
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class TeamGoalInfoVO(
    @Column(nullable = false)
    val detail: String,
    @Column(nullable = false)
    val color: String
) {
    fun toDomain() = TeamGoalInfo(
        detail = detail,
        color = color
    )

    companion object {
        fun from(teamGoalInfo: TeamGoalInfo) = TeamGoalInfoVO(
            detail = teamGoalInfo.detail,
            color = teamGoalInfo.color
        )
    }
}