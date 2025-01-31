package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUser
import java.time.LocalDateTime

class Chat(
    val id: ChatId = ChatId(),
    val teamId: TeamId,
    val sender: Sender,
    private var _chatInfo: ChatInfo,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
) : Base(
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
        _chatInfo = chatInfo,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )

    val chatInfo: ChatInfo
        get() = _chatInfo

    fun updateMessage(message: String) {
        this._chatInfo = ChatInfo(
            message = message,
            type = _chatInfo.type
        )
    }
}