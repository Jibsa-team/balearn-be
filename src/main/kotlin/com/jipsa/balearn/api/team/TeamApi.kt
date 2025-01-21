package com.jipsa.balearn.api.team

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.team.dto.*
import com.jipsa.balearn.api.team_user.dto.TeamUserReadResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamService
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/team")
class TeamApi(
    private val teamService: TeamService
) {
    @PostMapping("/create")
    fun createTeam(
        @RequestPart("data") request: TeamCreateRequest,
        @RequestPart("image", required = false) image: MultipartFile?,
        @CurrentUser user: User
    ): ApiResponse<TeamCreateResponse> {
        return ApiResponse.success(
            TeamCreateResponse.from(
                teamService.createTeam(
                    name = request.name,
                    description = request.description,
                    goals = request.goals,
                    image = image?.let { File.from(it) },
                    user = user
                )
            )
        )
    }

    @GetMapping("/{teamId}")
    fun readTeam(
        @PathVariable teamId: Long,
        @CurrentUser user: User
    ): ApiResponse<TeamResponse> {
        return ApiResponse.success(teamService.readTeam(TeamId(teamId), user.id))
    }

    @GetMapping("/list")
    fun readMyTeams(
        @CurrentUser user: User
    ): ApiResponse<List<TeamReadResponse>> {
        return ApiResponse.success(teamService.readMyTeams(user.id).map { TeamReadResponse.from(it) })
    }

    @GetMapping("/invite/{teamId}")
    fun inviteTeam(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
    ): ApiResponse<TeamInviteResponse> {
        return ApiResponse.success(TeamInviteResponse(teamService.invite(TeamId(teamId), user.id)))
    }

    @PostMapping("/join")
    fun joinTeam(
        @CurrentUser user: User,
        @RequestBody request: TeamJoinRequest
    ): ApiResponse<TeamUserReadResponse> {
        return ApiResponse.success(TeamUserReadResponse.from(teamService.joinTeam(request.inviteCode, user)))
    }

    @PutMapping("/{teamId}")
    fun updateTeam(
        @PathVariable teamId: Long,
        @RequestPart("data") request: TeamUpdateRequest,
        @RequestPart("image", required = false) image: MultipartFile?,
        @CurrentUser user: User
    ): ApiResponse<TeamReadResponse> {
        return ApiResponse.success(
            TeamReadResponse.from(
                teamService.updateTeam(
                    teamId = TeamId(teamId),
                    name = request.name,
                    description = request.description,
                    image = image?.let { File.from(it) },
                    user = user
                )
            )
        )
    }
}