package com.jipsa.balearn.domain.team_goal

import org.springframework.stereotype.Component

@Component
class TeamGoalUpdater(
    private val teamGoalRepository: TeamGoalRepository
) {
    fun update(teamGoal: TeamGoal, detail: String?, color: String?): TeamGoal {
        teamGoal.updateInfo(detail, color)
        return teamGoalRepository.save(teamGoal)
    }
}