package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.api.team_goal.dto.TeamGoalsUpdateRequest
import com.jipsa.balearn.domain.schedule.exception.CustomTeamGoalException
import org.springframework.stereotype.Component

@Component
class TeamGoalUpdater(
    private val teamGoalRepository: TeamGoalRepository
) {
    fun update(teamGoal: TeamGoal, detail: String?, color: String?): TeamGoal {
        teamGoal.updateInfo(detail, color)
        return teamGoalRepository.save(teamGoal)
    }

    fun updateAll(request: List<TeamGoalsUpdateRequest>?): List<TeamGoal>? {
        val teamGoals = request?.map {
            val teamGoal = teamGoalRepository.findById(TeamGoalId(it.id))
                ?: throw CustomTeamGoalException.TeamGoalNotFoundException
            teamGoal.updateInfo(it.detail, it.color)
            teamGoal
        }
        return teamGoals?.let { teamGoalRepository.saveAll(it) }
    }
}