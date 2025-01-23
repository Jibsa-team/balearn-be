package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamUserService(
    private val teamUserReader: TeamUserReader,
    private val teamUserValidator: TeamUserValidator,
    private val teamUserUpdater: TeamUserUpdater,
    private val teamUserImageAppender: TeamUserImageAppender,
    private val teamUserDeleter: TeamUserDeleter
) {
    fun readTeamUser(teamId: TeamId, userId: UserId): TeamUser {
        return teamUserReader.readBy(teamId, userId)
    }

    fun readTeamUser(teamId: TeamId, userId: UserId, teamUserId: TeamUserId): TeamUser {
        teamUserValidator.validTeamUser(teamId, userId)
        return teamUserReader.read(teamUserId)
    }

    @Transactional
    fun updateTeamUser(userId: UserId, teamId: TeamId, nickname: String?, image: File?): TeamUser {
        val teamUser = teamUserReader.readBy(teamId, userId)
        val profileImgUrl = image?.let { teamUserImageAppender.append(image) }
        return teamUserUpdater.update(
            teamUser = teamUser,
            nickname = nickname,
            profileImageUrl = profileImgUrl
        )
    }

    fun changeRole(userId: UserId, teamId: TeamId, teamUserId: TeamUserId, role: TeamUserRole): TeamUser {
        val teamUser = teamUserReader.read(teamUserId)

        teamUserValidator.validOwner(teamId, userId)

        return teamUserUpdater.update(teamUser, role)
    }

    fun changeRole(userId: UserId, teamId: TeamId, role: TeamUserRole): TeamUser {
        val teamUser = teamUserReader.readBy(teamId, userId)

        teamUserValidator.validOwner(teamId, userId)

        return teamUserUpdater.update(teamUser, role)
    }

    fun deleteTeamUser(userId: UserId, teamId: TeamId) {
        teamUserValidator.isOwner(teamId, userId)
        val teamUser = teamUserReader.readBy(teamId, userId)
        teamUserDeleter.delete(teamUser)
    }

    fun deleteTeamUser(userId: UserId, teamId: TeamId, teamUserId: TeamUserId) {
        teamUserValidator.validLeader(teamId, userId)
        val teamUser = teamUserReader.read(teamUserId)
        teamUserValidator.isOwner(teamId, teamUser.user.id)
        teamUserDeleter.delete(teamUser)
    }
}