package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Component

@Component
class TeamUserDeleter(
    private val teamUserRepository: TeamUserRepository,
) {
    fun delete(teamUserId: TeamUserId) {
        teamUserRepository.deleteById(teamUserId)
    }

    fun delete(teamUser: TeamUser) {
        teamUserRepository.delete(teamUser)
    }

    fun deleteBy(teamId: TeamId) {
        teamUserRepository.deleteByTeamId(teamId)
    }

    fun deleteBy(userId: UserId) {
        teamUserRepository.deleteByUserId(userId)
    }
}