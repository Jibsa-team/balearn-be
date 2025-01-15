package com.jipsa.balearn.database.team.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.UserId
import jakarta.persistence.*

@Entity
@Table(name = "teams")
class TeamEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Embedded
    val teamInfoVO: TeamInfoVO

) : BaseEntity() {
    fun toDomain() = Team(
        id = TeamId(id),
        _teamInfo = teamInfoVO.toDomain(),
        createdAt = createdAt,
        modifiedAt = modifiedAt,
        createdBy = createdBy?.let { UserId(it) },
        modifiedBy = modifiedBy?.let { UserId(it) }
    )

    companion object {
        fun from(team: Team) = TeamEntity(
            id = team.id.value,
            teamInfoVO = TeamInfoVO.from(team.teamInfo)
        )
    }
}