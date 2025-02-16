package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.exception.CustomTeamUserException
import com.jipsa.balearn.domain.user.UserId
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class TeamUserReader(
    private val teamUserRepository: TeamUserRepository,
    private val redisRepository: RedisRepository
) {
    fun read(teamUserId: TeamUserId): TeamUser {

        redisRepository.getTeamUser(teamUserId)?.let {
            return it
        }

        return teamUserRepository.findById(teamUserId)?.let {
            redisRepository.cacheTeamUser(it)
            it
        } ?: throw CustomTeamUserException.TeamUserNotFoundException
    }

    fun readBy(teamId: TeamId): List<TeamUser> {
        return redisRepository.getTeamUserByTeamId(teamId)?.mapNotNull {
            redisRepository.getTeamUser(it) ?: teamUserRepository.findById(it)?.let { teamUser ->
                redisRepository.cacheTeamUser(teamUser)
                teamUser
            }
        } ?: teamUserRepository.findByTeamId(teamId).onEach { teamUser ->
            redisRepository.cacheTeamUser(teamUser)
        }
    }

    fun readBy(userId: UserId): List<TeamUser> {
        return redisRepository.getTeamUserByUserId(userId)?.mapNotNull {
            redisRepository.getTeamUser(it) ?: teamUserRepository.findById(it)?.let { teamUser ->
                redisRepository.cacheTeamUser(teamUser)
                teamUser
            }
        } ?: teamUserRepository.findByUserId(userId).onEach { teamUser ->
            redisRepository.cacheTeamUser(teamUser)
        }
    }

    fun readBy(teamId: TeamId, userId: UserId): TeamUser {
        return redisRepository.getTeamUserByTeamIdAndUserId(teamId, userId)?.let {
            redisRepository.getTeamUser(it) ?: teamUserRepository.findById(it)?.let { teamUser ->
                redisRepository.cacheTeamUser(teamUser)
                teamUser
            }
        } ?: teamUserRepository.findByTeamIdAndUserId(teamId, userId)?.let {
            redisRepository.cacheTeamUser(it)
            it
        }
        ?: throw CustomTeamUserException.TeamUserNotFoundException
    }
}