package com.jipsa.balearn.database.team_user.entity

import com.jipsa.balearn.domain.team_user.TeamUserProfile
import com.jipsa.balearn.domain.team_user.TeamUserRole
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class TeamUserProfileVO(
    @Column(nullable = false)
    val nickname: String,
    @Column(nullable = false)
    val profileImageUrl: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: TeamUserRole
) {
    fun toDomain() = TeamUserProfile(
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        role = role
    )

    companion object {
        fun from(teamUserProfile: TeamUserProfile) = TeamUserProfileVO(
            nickname = teamUserProfile.nickname,
            profileImageUrl = teamUserProfile.profileImageUrl,
            role = teamUserProfile.role
        )
    }
}