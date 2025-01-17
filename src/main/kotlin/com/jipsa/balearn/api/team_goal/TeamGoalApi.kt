package com.jipsa.balearn.api.team_goal

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.team_goal.dto.TeamGoalCreateRequest
import com.jipsa.balearn.api.team_goal.dto.TeamGoalReadResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_goal.TeamGoalService
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/team/goal")
class TeamGoalApi(
    private val teamGoalService: TeamGoalService
) {
    @PostMapping("/create")
    fun createTeamGoal(
        @CurrentUser user: User,
        @RequestBody request: TeamGoalCreateRequest
    ): ApiResponse<TeamGoalReadResponse> {
        return ApiResponse.success(
            TeamGoalReadResponse.from(
                teamGoalService.appendTeamGoal(
                    detail = request.detail,
                    color = request.color,
                    userId = user.id,
                    teamId = TeamId(request.teamId)
                )
            )
        )
    }
}