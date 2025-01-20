package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.domain.schedule.exception.CustomTeamGoalException
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component

@Component
class TeamGoalReader(
    private val teamGoalRepository: TeamGoalRepository
) {
    fun readBy(teamId: TeamId): List<TeamGoal> {
        return teamGoalRepository.findByTeamId(teamId)
    }

    fun read(teamGoalId: TeamGoalId): TeamGoal {
        return teamGoalRepository.findById(teamGoalId)
            ?: throw CustomTeamGoalException.TeamGoalNotFoundException
    }
}