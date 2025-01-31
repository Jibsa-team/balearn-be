package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.domain.team_user.TeamUserRole

@JvmInline
value class ChatId(val value: Long = 0)

data class ChatInfo(
    val message: String,
    val type: ChatType
)

enum class ChatType(type: String) {
    ENTER("입장"),
    EXIT("퇴장"),
    TALK("대화")
}

data class Sender(
    val id: TeamUserId,
    val nickname: String,
    val profileImageUrl: String,
    val role: TeamUserRole
) {
    companion object {
        fun from(teamUser: TeamUser): Sender {
            return Sender(
                id = teamUser.id,
                nickname = teamUser.profile.nickname,
                profileImageUrl = teamUser.profile.profileImageUrl,
                role = teamUser.profile.role
            )
        }
    }
}