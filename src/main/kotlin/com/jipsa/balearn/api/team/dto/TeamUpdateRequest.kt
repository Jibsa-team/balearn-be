package com.jipsa.balearn.api.team.dto

import com.jipsa.balearn.api.team_goal.dto.TeamGoalsUpdateRequest

data class TeamUpdateRequest(
    val name: String?,
    val description: String?,
    val goals: List<TeamGoalsUpdateRequest>?
) {
}