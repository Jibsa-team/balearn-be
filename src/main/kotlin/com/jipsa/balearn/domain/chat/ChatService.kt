package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamReader
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

@Service
class ChatService(
    private val chatPublisher: ChatPublisher,
    private val teamUserReader: TeamUserReader,
    private val teamUserValidator: TeamUserValidator,
    private val chatAppender: ChatAppender,
    private val chatReader: ChatReader,
    private val teamReader: TeamReader,
    private val chatDeleter: ChatDeleter
) {
    @Transactional
    fun sendChat(teamId: TeamId, message: String, user: User) {
        val teamUser = teamUserReader.readBy(teamId, user.id)
        val now = LocalDateTime.now()
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
            return chatList + chatReader.readInElasticsearch(
                teamId,
                cursor,
                PageRequest.of(0, size - chatList.size, Sort.by(Sort.Direction.DESC, "createdAt"))
            )
        }
        return chatList
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    fun appendChatInElasticsearch() {
        chatAppender.appendInElasticsearchBatch(chatReader.readInRedisBatch())
        chatDeleter.deleteChatInRedisBatch()
    }

//    @Transactional
//    @Scheduled(cron = "0 0 4 */3 * ?")
//    fun syncChat() {
//        val teamList = teamReader.readAll()
//
//        teamList.forEach { team ->
//            val chatList = chatReader.readInElasticsearch(teamId = team.id, pageable = Pageable.ofSize(100))
//            chatList.isNotEmpty().let {
//                val oldChatList = chatReader.readInRedis(team.id, null, Pageable.ofSize(100))
//
//                chatAppender.appendInRedisBatch(team.id, chatList)
//            }
//        }
//    }

    @Scheduled(fixedRate = 360000)
    fun decreaseChatInRedis() {
        chatDeleter.decreaseChatInRedis()
    }
}