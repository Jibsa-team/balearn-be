package com.jipsa.balearn.domain.team_user

import com.fasterxml.jackson.annotation.JsonValue

@JvmInline
value class TeamUserId(@JsonValue val value: Long = 0)


data class TeamUserProfile(
    val nickname: String,
    val profileImageUrl: String,
    val role: TeamUserRole
)

enum class TeamUserRole(name: String) {
    OWNER("소유자"),
    LEADER("리더"),
    MEMBER("멤버"),
    EX_MEMBER("이전 멤버")
}