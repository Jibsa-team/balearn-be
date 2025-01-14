package com.jipsa.balearn.database.team_goal.repository

import com.jipsa.balearn.database.team_goal.entity.TeamGoalEntity
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_goal.TeamGoal
import com.jipsa.balearn.domain.team_goal.TeamGoalId
import com.jipsa.balearn.domain.team_goal.TeamGoalRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class TeamGoalRepositoryAdaptor(
    private val teamGoalJpaRepository: TeamGoalJpaRepository
) : TeamGoalRepository {
    override fun save(teamGoal: TeamGoal): TeamGoal {
        return teamGoalJpaRepository.save(TeamGoalEntity.from(teamGoal)).toDomain()
    }

    override fun saveAll(teamGoals: List<TeamGoal>): List<TeamGoal> {
        return teamGoalJpaRepository.saveAll(teamGoals.map { TeamGoalEntity.from(it) }).map { it.toDomain() }
    }

    override fun findByTeamId(teamId: TeamId): List<TeamGoal> {
        return teamGoalJpaRepository.findByTeam_Id(teamId.value).map { it.toDomain() }
    }

    override fun findById(teamGoalId: TeamGoalId): TeamGoal? {
        return teamGoalJpaRepository.findById(teamGoalId.value).getOrNull()?.toDomain()
    }

    override fun delete(teamGoal: TeamGoal) {
        teamGoalJpaRepository.delete(TeamGoalEntity.from(teamGoal))
    }

    override fun deleteById(teamGoalId: TeamGoalId) {
        teamGoalJpaRepository.deleteById(teamGoalId.value)
    }
}