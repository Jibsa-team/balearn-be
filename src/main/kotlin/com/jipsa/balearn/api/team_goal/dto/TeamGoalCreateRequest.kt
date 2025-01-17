package com.jipsa.balearn.api.team_goal.dto

data class TeamGoalCreateRequest(
    val teamId: Long,
    val detail: String,
    val color: String,
) {
}