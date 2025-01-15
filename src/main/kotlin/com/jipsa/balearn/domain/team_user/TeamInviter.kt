package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.exception.CustomTeamUserException
import com.jipsa.balearn.domain.user.UserId
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class TeamInviter(
    private val teamUserRepository: TeamUserRepository,
    private val redisRepository: RedisRepository
) {

    fun inviteUser(teamId: TeamId, userId: UserId): String {
        val teamUser = teamUserRepository.findByTeamIdAndUserId(teamId, userId)
            ?: throw CustomTeamUserException.TeamUserNotFoundException
        teamUser.isLeader()
        val code = generateRandomString()
        redisRepository.saveValue(
            redisRepository.generateTeamInviteCodeKey(code),
            teamId.value.toString(),
            BalearnConstants.TEAM_INVITE_TIME
        )
        return code
    }

    private fun generateRandomString(length: Int = 6): String {
        val chars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}