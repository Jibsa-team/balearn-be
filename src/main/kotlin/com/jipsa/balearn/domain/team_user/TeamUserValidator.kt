package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.exception.CustomTeamUserException
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Component

// TODO : 캐싱하는 쪽으로 리팩토링
@Component
class TeamUserValidator(
    private val teamUserRepository: TeamUserRepository,
) {
    fun isExistTeamUser(teamId: TeamId, userId: UserId) {
        if (teamUserRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw CustomTeamUserException.TeamUserAlreadyExistException
        }
    }

    fun validTeamUser(teamId: TeamId, userId: UserId) {
        if (!teamUserRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw CustomTeamUserException.TeamUserNotValidException
        }
    }

    fun validLeader(teamId: TeamId, userId: UserId) {
        teamUserRepository.findByTeamIdAndUserId(teamId, userId)?.isLeader()
            ?: throw CustomTeamUserException.TeamUserPermissionDeniedException
    }

    fun validOwner(teamId: TeamId, userId: UserId) {
        teamUserRepository.findByTeamIdAndUserId(teamId, userId)?.isOwner()
            ?: throw CustomTeamUserException.TeamUserPermissionDeniedException
    }

    fun isExistTeamOwner(userId: UserId) {
        if (teamUserRepository.existsByUserIdAndRole(userId, TeamUserRole.OWNER)) {
            throw CustomTeamUserException.OwnerCannotLeaveException
        }
    }

    fun isOwner(teamId: TeamId, userId: UserId) {
        if (teamUserRepository.existsByTeamIdAndUserIdAndRole(teamId, userId, TeamUserRole.OWNER)) {
            throw CustomTeamUserException.OwnerCannotLeaveException
        }
    }
}