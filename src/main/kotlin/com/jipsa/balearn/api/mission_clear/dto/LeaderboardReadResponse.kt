package com.jipsa.balearn.api.mission_clear.dto

data class LeaderboardReadResponse(
    val leaderboard: List<LeaderboardResponse>,
    val myRank: LeaderboardResponse
) {
}