package com.jipsa.balearn.api.team_user.dto

import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserRole
import java.time.LocalDateTime

data class TeamUserReadResponse(
    val id: Long,
    val userId: Long,
    val role: TeamUserRole,
    val nickname: String,
    val imgUrl: String,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
) {
    companion object {
        fun from(teamUser: TeamUser): TeamUserReadResponse {
            return TeamUserReadResponse(
                id = teamUser.id.value,
                userId = teamUser.user.id.value,
                role = teamUser.profile.role,
                nickname = teamUser.profile.nickname,
                imgUrl = teamUser.profile.profileImageUrl,
                createdAt = teamUser.createdAt,
                modifiedAt = teamUser.modifiedAt
            )
        }
    }
}