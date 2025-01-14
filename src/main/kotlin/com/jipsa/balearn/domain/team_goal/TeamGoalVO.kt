package com.jipsa.balearn.domain.team_goal

@JvmInline
value class TeamGoalId(val value: Long = 0)

data class TeamGoalInfo(
    val detail: String,
    val color: String
)