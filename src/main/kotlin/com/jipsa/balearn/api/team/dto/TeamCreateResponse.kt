package com.jipsa.balearn.api.team.dto

import com.jipsa.balearn.domain.team.Team

data class TeamCreateResponse(
    val teamId: Long
) {
    companion object {
        fun from(team: Team): TeamCreateResponse {
            return TeamCreateResponse(team.id.value)
        }
    }
}