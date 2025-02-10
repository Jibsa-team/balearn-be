package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUser
import java.io.Serializable
import java.time.LocalDateTime

class Chat(
    val id: ChatId = ChatId(),
    val teamId: TeamId,
    val sender: Sender,
    val chatInfo: ChatInfo,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
) : Serializable, Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt
) {
    constructor(
        id: ChatId = ChatId(),
        teamUser: TeamUser,
        chatInfo: ChatInfo,
        createdAt: LocalDateTime? = null,
        modifiedAt: LocalDateTime? = null,
    ) : this(
        id = id,
        teamId = teamUser.team.id,
        sender = Sender.from(teamUser),
        chatInfo = chatInfo,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}