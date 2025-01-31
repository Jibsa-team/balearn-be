package com.jipsa.balearn.domain.team_goal

import com.jipsa.balearn.domain.team.TeamId

interface TeamGoalRepository {
    fun save(teamGoal: TeamGoal): TeamGoal
    fun saveAll(teamGoals: List<TeamGoal>): List<TeamGoal>
    fun findByTeamId(teamId: TeamId): List<TeamGoal>
    fun findById(teamGoalId: TeamGoalId): TeamGoal?
    fun delete(teamGoal: TeamGoal)
    fun deleteById(teamGoalId: TeamGoalId)
    fun deleteByTeamId(teamId: TeamId)
    fun deleteAllById(teamGoals: List<TeamGoalId>)
}