package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.UserId
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class TeamUserDeleter(
    private val teamUserRepository: TeamUserRepository,
    private val redisRepository: RedisRepository
) {
    fun delete(teamUserId: TeamUserId) {
        redisRepository.deleteTeamUserCache(teamUserId)
        teamUserRepository.deleteById(teamUserId)
    }

    fun delete(teamUser: TeamUser) {
        redisRepository.getTeamUser(teamUser.id)?.let {
            redisRepository.deleteTeamUserCache(it.id)
        }
        teamUserRepository.delete(teamUser)
    }

    fun deleteBy(teamId: TeamId) {
        redisRepository.getTeamUserByTeamId(teamId)?.forEach(
            redisRepository::deleteTeamUserCache
        )
        teamUserRepository.deleteByTeamId(teamId)
    }

    fun deleteBy(userId: UserId) {
        redisRepository.getTeamUserByUserId(userId)?.forEach(
            redisRepository::deleteTeamUserCache
        )
        teamUserRepository.deleteByUserId(userId)
    }
}