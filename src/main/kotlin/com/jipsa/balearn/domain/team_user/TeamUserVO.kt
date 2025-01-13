package com.jipsa.balearn.domain.team_user

@JvmInline
value class TeamUserId(val value: Long)

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