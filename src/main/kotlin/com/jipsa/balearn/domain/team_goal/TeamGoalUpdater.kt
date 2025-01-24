package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.api.team_goal.dto.TeamGoalsUpdateRequest
import com.jipsa.balearn.domain.schedule.exception.CustomTeamGoalException
import com.jipsa.balearn.domain.team.Team
import org.springframework.stereotype.Component

@Component
class TeamGoalUpdater(
    private val teamGoalRepository: TeamGoalRepository
) {
    fun update(teamGoal: TeamGoal, detail: String?, color: String?): TeamGoal {
        teamGoal.updateInfo(detail, color)
        return teamGoalRepository.save(teamGoal)
    }

    fun updateAll(requests: List<TeamGoalsUpdateRequest>?, team: Team): List<TeamGoal>? {
        val teamGoals = requests?.map { request ->
            request.id?.let {
                val teamGoal = teamGoalRepository.findById(TeamGoalId(it))
                    ?: throw CustomTeamGoalException.TeamGoalNotFoundException
                teamGoal.updateInfo(request.detail, request.color)
                teamGoal
            } ?: TeamGoal(
                _teamGoalInfo = TeamGoalInfo(
                    detail = request.detail ?: throw IllegalArgumentException("팀 목표를 작성해야합니다."),
                    color = request.color ?: throw IllegalArgumentException("팀 목표 색상을 작성해야합니다.")
                ),
                team = team
            )

        }
        return teamGoals?.let { teamGoalRepository.saveAll(it) }
    }
}