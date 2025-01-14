package com.jipsa.balearn.database.team_goal.repository

import com.jipsa.balearn.database.team_goal.entity.TeamGoalEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TeamGoalJpaRepository : JpaRepository<TeamGoalEntity, Long> {
    fun findByTeam_Id(teamId: Long): List<TeamGoalEntity>
}