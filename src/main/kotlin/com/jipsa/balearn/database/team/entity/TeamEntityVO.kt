package com.jipsa.balearn.database.team.entity

import com.jipsa.balearn.domain.team.TeamInfo
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class TeamInfoVO(
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val description: String,
    @Column(nullable = false)
    val teamImageUrl: String = "https://cdn.balearn.o-r.kr/profile/default-team.png"
) {
    fun toDomain() = TeamInfo(
        name = name,
        description = description,
        teamImageUrl = teamImageUrl
    )

    companion object {
        fun from(teamInfo: TeamInfo) = TeamInfoVO(
            name = teamInfo.name,
            description = teamInfo.description,
            teamImageUrl = teamInfo.teamImageUrl
        )
    }
}