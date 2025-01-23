package com.jipsa.balearn.api.mission_clear.dto

import com.jipsa.balearn.domain.team_user.TeamUser

data class LeaderboardResponse(
    val rank: Int,
    val nickname: String,
    val profileImgUrl: String,
    val score: Int
) {
    companion object {
        fun from(rank: Int, teamUser: TeamUser, score: Int): LeaderboardResponse {
            return LeaderboardResponse(
                rank = rank,
                nickname = teamUser.profile.nickname,
                profileImgUrl = teamUser.profile.profileImageUrl,
                score = score
            )
        }
    }
}