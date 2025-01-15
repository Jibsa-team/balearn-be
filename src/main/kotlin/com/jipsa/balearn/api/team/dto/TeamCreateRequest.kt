package com.jipsa.balearn.api.team.dto

import com.jipsa.balearn.domain.team_goal.TeamGoalInfo

data class TeamCreateRequest(
    val name: String,
    val description: String,
    val goals: List<TeamGoalInfo>? = null
) {
}