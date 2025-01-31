package com.jipsa.balearn.api.mission_clear

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.mission_clear.dto.LeaderboardResponse
import com.jipsa.balearn.api.mission_clear.dto.MissionClearResponse
import com.jipsa.balearn.api.mission_clear.dto.MissionListRequest
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.mission_clear.MissionClearService
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/leaderboard")
class LeaderboardApi(
    private val missionClearService: MissionClearService
) {
    @GetMapping("/{teamId}")
    fun readLeaderboard(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
        @RequestParam(required = false) top: Int?
    ): ApiResponse<List<LeaderboardResponse>> {
        return ApiResponse.success(missionClearService.readLeaderboard(TeamId(teamId), top ?: 5))
    }

    @PostMapping("/{teamId}/clear/{missionId}")
    fun createClear(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
        @PathVariable missionId: Long
    ): ApiResponse<MissionClearResponse> {
        return ApiResponse.success(
            MissionClearResponse.from(
                missionClearService.appendMissionClear(TeamId(teamId), user, MissionId(missionId))
            )
        )
    }

    @PostMapping("/{teamId}/clear")
    fun createClear(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
        @RequestBody missionList: MissionListRequest
    ): ApiResponse<Unit> {
        return ApiResponse.success(
            missionClearService.appendMissionClears(
                teamId = TeamId(teamId),
                user = user,
                createIds = missionList.createIds?.map { MissionId(it) },
                deleteIds = missionList.deleteIds?.map { MissionId(it) }
            )
        )
    }

    @DeleteMapping("/{teamId}/clear/{missionId}")
    fun deleteClear(
        @CurrentUser user: User,
        @PathVariable teamId: Long,
        @PathVariable missionId: Long
    ): ApiResponse<Unit> {
        missionClearService.deleteMissionClear(TeamId(teamId), user, MissionId(missionId))
        return ApiResponse.success()
    }

}