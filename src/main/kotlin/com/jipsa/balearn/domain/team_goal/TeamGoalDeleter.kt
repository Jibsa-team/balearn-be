package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component

@Component
class TeamGoalDeleter(
    private val teamGoalRepository: TeamGoalRepository
) {
    fun delete(teamGoal: TeamGoal) {
        teamGoalRepository.delete(teamGoal)
    }

    fun delete(teamGoalId: TeamGoalId) {
        teamGoalRepository.deleteById(teamGoalId)
    }

    fun deleteBy(teamId: TeamId) {
        teamGoalRepository.deleteByTeamId(teamId)
    }

    fun deleteAllBy(teamGoalIds: List<TeamGoalId>) {
        teamGoalRepository.deleteAllById(teamGoalIds)
    }
}