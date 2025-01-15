package com.jipsa.balearn.domain.team_goal

import org.springframework.stereotype.Component

@Component
class TeamGoalAppender(
    private val teamGoalRepository: TeamGoalRepository
) {
    fun append(teamGoal: TeamGoal): TeamGoal {
        return teamGoalRepository.save(teamGoal)
    }

    fun appendAll(teamGoals: List<TeamGoal>): List<TeamGoal> {
        return teamGoalRepository.saveAll(teamGoals)
    }
}