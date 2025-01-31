package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.schedule.exception.CustomTeamException
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamRepository
import com.jipsa.balearn.domain.team_user.exception.CustomTeamUserException
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import org.springframework.stereotype.Component

@Component
class TeamUserAppender(
    private val teamUserRepository: TeamUserRepository,
    private val teamRepository: TeamRepository,
    private val redisRepository: RedisRepository,
    private val teamUserValidator: TeamUserValidator
) {
    fun append(teamUser: TeamUser): TeamUser {
        teamUserValidator.isExistTeamUser(teamUser.team.id, teamUser.user.id)

        val newTeamUser = teamUserRepository.save(teamUser)

        redisRepository.addZSetScore(
            key = redisRepository.generateLeaderboardKey(newTeamUser.team.id.value),
            value = newTeamUser.id.value.toString(),
            score = 0.0
        )
        return newTeamUser
    }

    fun join(inviteCode: String, user: User): TeamUser {
        val teamId = redisRepository.getValue(redisRepository.generateTeamInviteCodeKey(inviteCode.uppercase()))
            ?.let { TeamId(it.toLong()) }
            ?: throw CustomTeamUserException.InviteCodeNotValidException

        val team = teamRepository.findById(teamId)
            ?: throw CustomTeamException.TeamNotFoundException

        val teamUser = TeamUser.from(
            team = team,
            user = user,
            role = TeamUserRole.MEMBER
        )
        return append(teamUser)
    }
}