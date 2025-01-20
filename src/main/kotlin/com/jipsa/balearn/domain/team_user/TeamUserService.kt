package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Service

@Service
class TeamUserService(
    private val teamUserReader: TeamUserReader,
    private val teamUserValidator: TeamUserValidator,
    private val teamUserUpdater: TeamUserUpdater,
    private val teamUserImageAppender: TeamUserImageAppender
) {
    fun readTeamUser(teamId: TeamId, userId: UserId): TeamUser {
        return teamUserReader.readBy(teamId, userId)
    }

    fun readTeamUser(teamId: TeamId, userId: UserId, teamUserId: TeamUserId): TeamUser {
        teamUserValidator.validTeamUser(teamId, userId)
        return teamUserReader.read(teamUserId)
    }

    fun updateTeamUser(userId: UserId, teamId: TeamId, nickname: String?, image: File?): TeamUser {
        val teamUser = teamUserReader.readBy(teamId, userId)
        val profileImgUrl = image?.let { teamUserImageAppender.append(image) }
        return teamUserUpdater.update(
            teamUser = teamUser,
            nickname = nickname,
            profileImageUrl = profileImgUrl
        )
    }
}