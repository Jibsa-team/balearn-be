package com.jipsa.balearn.api.team

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.team.dto.TeamCreateRequest
import com.jipsa.balearn.api.team.dto.TeamCreateResponse
import com.jipsa.balearn.api.team.dto.TeamReadResponse
import com.jipsa.balearn.api.team.dto.TeamResponse
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
}