package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.exception.CustomTeamUserException
import com.jipsa.balearn.domain.user.UserId
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class TeamUserValidator(
    private val teamUserRepository: TeamUserRepository,
    private val redisRepository: RedisRepository
) {
    fun isExistTeamUser(teamId: TeamId, userId: UserId) {
        if (redisRepository.getTeamUserByTeamIdAndUserId(
                teamId,
                userId
            ) != null || teamUserRepository.existsByTeamIdAndUserId(teamId, userId)
        ) {
            throw CustomTeamUserException.TeamUserAlreadyExistException
        }
    }

    fun validTeamUser(teamId: TeamId, userId: UserId) {
        if (redisRepository.getTeamUserByTeamIdAndUserId(
                teamId,
                userId
            ) == null && !teamUserRepository.existsByTeamIdAndUserId(teamId, userId)
        ) {
            throw CustomTeamUserException.TeamUserNotValidException
        }
    }

    fun validLeader(teamId: TeamId, userId: UserId) {
        redisRepository.getTeamUserByTeamIdAndUserId(teamId, userId)?.let {
            redisRepository.getTeamUser(it)?.isLeader()
        } ?: teamUserRepository.findByTeamIdAndUserId(teamId, userId)?.let {
            redisRepository.cacheTeamUser(it)
            it.isLeader()
        } ?: throw CustomTeamUserException.TeamUserPermissionDeniedException
    }

    fun validOwner(teamId: TeamId, userId: UserId) {
        redisRepository.getTeamUserByTeamIdAndUserId(teamId, userId)?.let {
            redisRepository.getTeamUser(it)?.isOwner()
        } ?: teamUserRepository.findByTeamIdAndUserId(teamId, userId)?.let {
            redisRepository.cacheTeamUser(it)
            it.isOwner()
        } ?: throw CustomTeamUserException.TeamUserPermissionDeniedException
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