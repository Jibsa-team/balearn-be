package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamReader
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamGoalService(
    private val teamGoalAppender: TeamGoalAppender,
    private val teamUserValidator: TeamUserValidator,
    private val teamGoalReader: TeamGoalReader,
    private val teamReader: TeamReader,
    private val teamGoalUpdater: TeamGoalUpdater,
    private val teamGoalDeleter: TeamGoalDeleter
) {
    @Transactional
    fun appendTeamGoal(detail: String, color: String, teamId: TeamId, userId: UserId): TeamGoal {
        val team = teamReader.read(teamId)

        teamUserValidator.validLeader(teamId, userId)

        val teamGoal = TeamGoal(
            _teamGoalInfo = TeamGoalInfo(
                detail = detail,
                color = color
            ),
            team = team
        )

        return teamGoalAppender.append(teamGoal)
    }

    fun readTeamGoals(userId: UserId, teamId: TeamId): List<TeamGoal> {
        teamUserValidator.validTeamUser(teamId, userId)
        return teamGoalReader.readBy(teamId)
    }

    fun readTeamGoal(userId: UserId, teamGoalId: TeamGoalId): TeamGoal {
        val teamGoal = teamGoalReader.read(teamGoalId)
        teamUserValidator.validTeamUser(teamGoal.team.id, userId)
        return teamGoal
    }

    @Transactional
    fun updateTeamGoal(userId: UserId, teamGoalId: TeamGoalId, detail: String?, color: String?): TeamGoal {
        val teamGoal = teamGoalReader.read(teamGoalId)
        teamUserValidator.validLeader(teamGoal.team.id, userId)

        teamGoalUpdater.update(teamGoal, detail, color)

        return teamGoal
    }

    @Transactional
    fun deleteTeamGoal(userId: UserId, teamGoalId: TeamGoalId) {
        val teamGoal = teamGoalReader.read(teamGoalId)
        teamUserValidator.validLeader(teamGoal.team.id, userId)

        teamGoalDeleter.delete(teamGoal)
    }
}