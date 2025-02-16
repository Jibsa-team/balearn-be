package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class TeamUserUpdater(
    private val teamUserRepository: TeamUserRepository,
    private val redisRepository: RedisRepository
) {
    fun update(teamUser: TeamUser, nickname: String?, profileImageUrl: String?): TeamUser {

        teamUser.updateProfile(
            nickname = nickname,
            profileImageUrl = profileImageUrl
        )

        redisRepository.cacheTeamUser(teamUser)

        return teamUserRepository.save(teamUser)
    }

    fun update(teamUser: TeamUser, role: TeamUserRole): TeamUser {

        teamUser.changeRole(role)

        redisRepository.cacheTeamUser(teamUser)

        return teamUserRepository.save(teamUser)
    }
}