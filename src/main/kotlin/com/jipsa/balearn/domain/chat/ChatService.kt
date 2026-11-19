package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserReader
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.infra.redis.ChatPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ChatService(
    private val chatPublisher: ChatPublisher,
    private val teamUserReader: TeamUserReader,
    private val teamUserValidator: TeamUserValidator,
    private val chatAppender: ChatAppender,
    private val chatReader: ChatReader,
    private val chatDeleter: ChatDeleter,
    private val chatMaintenance: ChatMaintenance
) {
    @Transactional
    fun sendChat(teamId: TeamId, message: String, user: User) {
        val teamUser = teamUserReader.readBy(teamId, user.id)
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
        val chat = Chat(
            teamUser = teamUser,
            chatInfo = ChatInfo(
                message,
                ChatType.TALK
            ),
            createdAt = now,
            modifiedAt = now
        )
        chatAppender.appendInRedis(chat)
        chatPublisher.publish(chat)
    }

    fun getChatList(user: User, teamId: TeamId, cursor: LocalDateTime?, size: Int): List<Chat> {
        teamUserValidator.validTeamUser(teamId, user.id)
        val chatList =
            chatReader.readInRedis(teamId, cursor, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")))
        if (chatList.size < size) {
            val newCursor = chatList.lastOrNull()?.createdAt
            return chatList + chatReader.readInElasticsearch(
                teamId,
                newCursor ?: cursor,
                PageRequest.of(0, size - chatList.size, Sort.by(Sort.Direction.DESC, "createdAt"))
            )
        }
        return chatList
    }

    @Scheduled(fixedDelay = 60_000)
    fun appendChatInElasticsearch() {
        chatMaintenance.withLock {
            repeat(20) {
                val batch = chatReader.readInRedisBatch()
                if (batch.isEmpty()) return@withLock
                chatAppender.appendInElasticsearchBatch(batch)
                chatDeleter.acknowledgeChatBatch(batch.size.toLong())
            }
        }
    }

    @Scheduled(fixedDelay = 360_000)
    fun decreaseChatInRedis() {
        chatMaintenance.withLock { chatDeleter.decreaseChatInRedis() }
    }
}
