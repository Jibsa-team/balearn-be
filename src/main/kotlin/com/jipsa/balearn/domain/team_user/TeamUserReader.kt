package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.exception.CustomTeamUserException
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Component

@Component
class TeamUserReader(
    private val teamUserRepository: TeamUserRepository
) {
    fun read(teamUserId: TeamUserId): TeamUser {
        return teamUserRepository.findById(teamUserId) ?: throw CustomTeamUserException.TeamUserNotFoundException
    }

    fun readBy(teamId: TeamId): List<TeamUser> {
        return teamUserRepository.findByTeamId(teamId)
    }

    fun readBy(userId: UserId): List<TeamUser> {
        return teamUserRepository.findByUserId(userId)
    }

    fun readBy(teamId: TeamId, userId: UserId): TeamUser {
        return teamUserRepository.findByTeamIdAndUserId(teamId, userId)
            ?: throw CustomTeamUserException.TeamUserNotFoundException
    }

    fun validTeamUser(teamId: TeamId, userId: UserId) {
        if (!teamUserRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw CustomTeamUserException.TeamUserNotValidException
        }
    }
}