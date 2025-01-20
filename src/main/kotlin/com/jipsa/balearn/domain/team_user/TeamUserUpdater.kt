package com.jipsa.balearn.domain.team_user

import org.springframework.stereotype.Component

@Component
class TeamUserUpdater(
    private val teamUserRepository: TeamUserRepository,
) {
    fun update(teamUser: TeamUser, nickname: String?, profileImageUrl: String?): TeamUser {

        teamUser.updateProfile(
            nickname = nickname,
            profileImageUrl = profileImageUrl
        )

        return teamUserRepository.save(teamUser)
    }

    fun update(teamUser: TeamUser, role: TeamUserRole): TeamUser {

        teamUser.changeRole(role)

        return teamUserRepository.save(teamUser)
    }
}