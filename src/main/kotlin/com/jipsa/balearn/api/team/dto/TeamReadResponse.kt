package com.jipsa.balearn.api.team.dto

import com.jipsa.balearn.domain.team.Team
import java.time.LocalDateTime

data class TeamReadResponse(
    val id: Long,
    val name: String,
    val description: String,
    val imgUrl: String,
    val createdAt: LocalDateTime?,
    val createdBy: Long?,
    val modifiedAt: LocalDateTime?,
    val modifiedBy: Long?
) {
    companion object {
        fun from(team: Team): TeamReadResponse {
            return TeamReadResponse(
                id = team.id.value,
                name = team.teamInfo.name,
                description = team.teamInfo.description,
                imgUrl = team.teamInfo.teamImageUrl,
                createdAt = team.createdAt,
                createdBy = team.createdBy?.value,
                modifiedAt = team.modifiedAt,
                modifiedBy = team.modifiedBy?.value
            )
        }
    }
}