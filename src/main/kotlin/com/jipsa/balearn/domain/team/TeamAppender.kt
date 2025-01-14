package com.jipsa.balearn.domain.team

import org.springframework.stereotype.Component

@Component
class TeamAppender(
    private val teamRepository: TeamRepository
) {
    fun append(team: Team): Team {
        return teamRepository.save(team)
    }
}