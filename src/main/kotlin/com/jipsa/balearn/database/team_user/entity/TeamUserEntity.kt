package com.jipsa.balearn.database.team_user.entity

import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.database.user.entity.UserEntity
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.domain.team_user.TeamUserProfile
import jakarta.persistence.*

@Entity
@Table(name = "team_users")
class TeamUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Embedded
    val profile: TeamUserProfile,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    val team: TeamEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity
) {
    fun toDomain() = TeamUser(
        id = TeamUserId(id),
        _profile = profile,
        team = team.toDomain(),
        user = user.toDomain(),
    )

    companion object {
        fun from(teamUser: TeamUser) = TeamUserEntity(
            id = teamUser.id.value,
            profile = teamUser.profile,
            team = TeamEntity.from(teamUser.team),
            user = UserEntity.from(teamUser.user)
        )
    }
}