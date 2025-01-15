package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.domain.schedule.exception.CustomTeamException
import com.jipsa.balearn.domain.team.Team
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
    private val redisRepository: RedisRepository
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