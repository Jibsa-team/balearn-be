package com.jipsa.balearn.api.team_user

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.team_user.dto.TeamUserReadResponse
import com.jipsa.balearn.api.team_user.dto.TeamUserUpdateRequest
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.domain.team_user.TeamUserRole
import com.jipsa.balearn.domain.team_user.TeamUserService
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/team")
class TeamUserApi(
    private val teamUserService: TeamUserService
) {
    @GetMapping("/{teamId}/me")
    fun getMyTeamUser(
        @CurrentUser user: User,
        @PathVariable teamId: Long
    ): ApiResponse<TeamUserReadResponse> {
        return ApiResponse.success(
            TeamUserReadResponse.from(
                teamUserService.readTeamUser(
                    teamId = TeamId(teamId),
                    userId = user.id
                )
            )
        )
    }

    @GetMapping("/{teamId}/user/{teamUserId}")
    fun getTeamUser(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
        @PathVariable teamUserId: Long
    ): ApiResponse<TeamUserReadResponse> {
        return ApiResponse.success(
            TeamUserReadResponse.from(
                teamUserService.readTeamUser(
                    teamId = TeamId(teamId),
                    userId = user.id,
                    teamUserId = TeamUserId(teamUserId)
                )
            )
        )
    }

    @PutMapping("/{teamId}/me")
    fun updateUser(
        @RequestPart("data") request: TeamUserUpdateRequest,
        @RequestPart("image", required = false) image: MultipartFile?,
        @PathVariable teamId: Long,
        @CurrentUser user: User
    ): ApiResponse<TeamUserReadResponse> {
        return ApiResponse.success(
            TeamUserReadResponse.from(
                teamUserService.updateTeamUser(
                    teamId = TeamId(teamId),
                    userId = user.id,
                    nickname = request.nickname,
                    image = image?.let { File.from(it) }
                )
            )
        )
    }

    @PutMapping("/{teamId}/user/{teamUserId}")
    fun changeUserRole(
        @PathVariable teamId: Long,
        @PathVariable teamUserId: Long,
        @RequestParam role: String,
        @CurrentUser user: User
    ): ApiResponse<TeamUserReadResponse> {
        val teamUserRole = when (role) {
            "leader" -> TeamUserRole.LEADER
            "member" -> TeamUserRole.MEMBER
            else -> throw IllegalArgumentException("존재하지 않는 팀 역할입니다.")
        }

        return ApiResponse.success(
            TeamUserReadResponse.from(
                teamUserService.changeRole(
                    teamId = TeamId(teamId),
                    userId = user.id,
                    teamUserId = TeamUserId(teamUserId),
                    role = teamUserRole
                )
            )
        )
    }

    @PutMapping("/{teamId}/user/{teamUserId}/owner")
    fun changeOwner(
        @PathVariable teamId: Long,
        @PathVariable teamUserId: Long,
        @CurrentUser user: User
    ): ApiResponse<TeamUserReadResponse> {
        teamUserService.changeRole(
            teamId = TeamId(teamId),
            userId = user.id,
            teamUserId = TeamUserId(teamUserId),
            role = TeamUserRole.OWNER
        )

        return ApiResponse.success(
            TeamUserReadResponse.from(
                teamUserService.changeRole(
                    teamId = TeamId(teamId),
                    userId = user.id,
                    role = TeamUserRole.LEADER
                )
            )
        )
    }

    @DeleteMapping("/{teamId}/user/{teamUserId}")
    fun deleteUser(
        @PathVariable teamId: Long,
        @PathVariable teamUserId: Long,
        @CurrentUser user: User
    ): ApiResponse<Unit> {
        teamUserService.deleteTeamUser(
            teamId = TeamId(teamId),
            userId = user.id,
            teamUserId = TeamUserId(teamUserId)
        )
        return ApiResponse.success()
    }

    @DeleteMapping("/{teamId}/me")
    fun leaveTeam(
        @PathVariable teamId: Long,
        @CurrentUser user: User
    ): ApiResponse<Unit> {
        teamUserService.deleteTeamUser(
            teamId = TeamId(teamId),
            userId = user.id
        )
        return ApiResponse.success()
    }
}