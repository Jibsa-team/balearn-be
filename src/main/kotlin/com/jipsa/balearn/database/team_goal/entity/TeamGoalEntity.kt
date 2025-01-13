package com.jipsa.balearn.database.team_goal.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.domain.team_goal.TeamGoal
import com.jipsa.balearn.domain.team_goal.TeamGoalId
import com.jipsa.balearn.domain.user.UserId
import jakarta.persistence.*

@Entity
@Table(name = "team_goals")
class TeamGoalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Embedded
    val teamGoalInfoVO: TeamGoalInfoVO,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val teamEntity: TeamEntity
) : BaseEntity() {

    fun toDomain() = TeamGoal(
        id = TeamGoalId(id),
        _teamGoalInfo = teamGoalInfoVO.toDomain(),
        team = teamEntity.toDomain(),
        createdAt = createdAt,
        modifiedAt = modifiedAt,
        createdBy = createdBy?.let { UserId(it) },
        modifiedBy = modifiedBy?.let { UserId(it) }
    )

    companion object {
        fun from(teamGoal: TeamGoal) = TeamGoalEntity(
            id = teamGoal.id.value,
            teamGoalInfoVO = TeamGoalInfoVO.from(teamGoal.teamGoalInfo),
            teamEntity = TeamEntity.from(teamGoal.team)
        )
    }
}