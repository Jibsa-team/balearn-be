package com.jipsa.balearn.domain.team

import com.jipsa.balearn.domain.schedule.exception.CustomTeamException
import org.springframework.stereotype.Component

@Component
class TeamReader(
    private val teamRepository: TeamRepository
) {
    fun read(teamId: TeamId): Team {
        return teamRepository.findById(teamId)
            ?: throw CustomTeamException.TeamNotFoundException
    }
}