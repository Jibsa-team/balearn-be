package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component

@Component
class TeamGoalReader(
    private val teamGoalRepository: TeamGoalRepository
) {
    fun readBy(teamId: TeamId): List<TeamGoal> {
        return teamGoalRepository.findByTeamId(teamId)
    }
}