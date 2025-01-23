package com.jipsa.balearn.api.mission_clear

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.mission_clear.dto.LeaderboardResponse
import com.jipsa.balearn.api.mission_clear.dto.MissionClearResponse
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
        @PathVariable teamId: Long
    ): ApiResponse<List<LeaderboardResponse>> {
        return ApiResponse.success(missionClearService.readLeaderboard(TeamId(teamId)))
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