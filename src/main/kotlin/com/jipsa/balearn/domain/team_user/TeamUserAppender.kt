package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team_user.exception.CustomTeamUserException
import org.springframework.stereotype.Component

@Component
class TeamUserAppender(
    private val teamUserRepository: TeamUserRepository
) {
    fun append(teamUser: TeamUser): TeamUser {
        isExistTeamUser(teamUser)
        return teamUserRepository.save(teamUser)
    }

    private fun isExistTeamUser(teamUser: TeamUser) {
        if (teamUserRepository.existsByTeamIdAndUserId(teamUser.team.id, teamUser.user.id)) {
            throw CustomTeamUserException.TeamUserAlreadyExistException
        }
    }
}