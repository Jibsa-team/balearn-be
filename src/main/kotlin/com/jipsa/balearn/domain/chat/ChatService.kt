package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserReader
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.infra.redis.ChatPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ChatService(
    private val chatPublisher: ChatPublisher,
    private val teamUserReader: TeamUserReader,
    private val chatAppender: ChatAppender
) {
    @Transactional
    fun sendChat(teamId: TeamId, message: String, user: User) {
        val teamUser = teamUserReader.readBy(teamId, user.id)
        val chat = Chat(
            teamUser = teamUser,
            chatInfo = ChatInfo(
                message,
                ChatType.TALK
            ),
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        chatAppender.append(chat)
        chatPublisher.publish(chat)
    }
}