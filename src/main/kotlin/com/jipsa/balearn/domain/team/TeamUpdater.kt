package com.jipsa.balearn.domain.team

import org.springframework.stereotype.Component

@Component
class TeamUpdater(
    private val teamRepository: TeamRepository
) {
    fun update(team: Team, name: String?, description: String?, imgUrl: String?): Team {
        team.updateTeamInfo(
            name = name,
            description = description,
            imgUrl = imgUrl
        )
        return teamRepository.save(team)
    }
}