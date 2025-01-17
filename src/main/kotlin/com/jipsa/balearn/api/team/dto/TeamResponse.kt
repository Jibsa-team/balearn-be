package com.jipsa.balearn.api.team.dto

import com.jipsa.balearn.api.notice.dto.NoticeReadResponse
import com.jipsa.balearn.api.schedule.dto.ScheduleResponse
import com.jipsa.balearn.api.team_goal.dto.TeamGoalReadResponse
import com.jipsa.balearn.api.team_user.dto.TeamUserReadResponse

data class TeamResponse(
    val team: TeamReadResponse,
    val notice: NoticeReadResponse?,
    val goal: List<TeamGoalReadResponse>,
    val teamUser: List<TeamUserReadResponse>,
    val weeklySchedule: List<ScheduleResponse>
) {
}