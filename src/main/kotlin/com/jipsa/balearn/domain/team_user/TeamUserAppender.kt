package com.jipsa.balearn.domain.team_user

import org.springframework.stereotype.Component

@Component
class TeamUserAppender(
    private val teamUserRepository: TeamUserRepository
) {
    fun append(teamUser: TeamUser): TeamUser {
        return teamUserRepository.save(teamUser)
    }

    fun appendAll(teamUsers: List<TeamUser>): List<TeamUser> {
        return teamUserRepository.saveAll(teamUsers)
    }
}