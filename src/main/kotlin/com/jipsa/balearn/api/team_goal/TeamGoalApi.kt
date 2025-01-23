package com.jipsa.balearn.api.team_goal

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.team_goal.dto.TeamGoalCreateRequest
import com.jipsa.balearn.api.team_goal.dto.TeamGoalReadResponse
import com.jipsa.balearn.api.team_goal.dto.TeamGoalUpdateRequest
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_goal.TeamGoalId
import com.jipsa.balearn.domain.team_goal.TeamGoalService
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/team")
class TeamGoalApi(
    private val teamGoalService: TeamGoalService
) {
    @PostMapping("/goal/create")
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

    @GetMapping("/goal/{teamGoalId}")
    fun getTeamGoal(
        @CurrentUser user: User,
        @PathVariable teamGoalId: Long
    ): ApiResponse<TeamGoalReadResponse> {
        return ApiResponse.success(
            TeamGoalReadResponse.from(
                teamGoalService.readTeamGoal(
                    teamGoalId = TeamGoalId(teamGoalId),
                    userId = user.id
                )
            )
        )
    }

    @GetMapping("/{teamId}/goal")
    fun getTeamGoals(
        @CurrentUser user: User,
        @PathVariable teamId: Long
    ): ApiResponse<List<TeamGoalReadResponse>> {
        return ApiResponse.success(
            teamGoalService.readTeamGoals(
                teamId = TeamId(teamId),
                userId = user.id
            ).map { TeamGoalReadResponse.from(it) }
        )
    }

    @PutMapping("/goal/{teamGoalId}")
    fun updateTeamGoal(
        @CurrentUser user: User,
        @PathVariable teamGoalId: Long,
        @RequestBody request: TeamGoalUpdateRequest
    ): ApiResponse<TeamGoalReadResponse> {
        return ApiResponse.success(
            TeamGoalReadResponse.from(
                teamGoalService.updateTeamGoal(
                    teamGoalId = TeamGoalId(teamGoalId),
                    detail = request.detail,
                    color = request.color,
                    userId = user.id
                )
            )
        )
    }

    @DeleteMapping("/goal/{teamGoalId}")
    fun deleteTeamGoal(
        @CurrentUser user: User,
        @PathVariable teamGoalId: Long
    ): ApiResponse<Unit> {
        teamGoalService.deleteTeamGoal(
            teamGoalId = TeamGoalId(teamGoalId),
            userId = user.id
        )
        return ApiResponse.success()
    }
}