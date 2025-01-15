package com.jipsa.balearn.domain.team_user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@JvmInline
value class TeamUserId(val value: Long = 0)


data class TeamUserProfile(
    val nickname: String,
    val profileImageUrl: String,
    val role: TeamUserRole
)

enum class TeamUserRole(name: String) {
    OWNER("소유자"),
    LEADER("리더"),
    MEMBER("멤버")
}