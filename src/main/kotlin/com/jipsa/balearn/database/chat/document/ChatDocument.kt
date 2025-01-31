package com.jipsa.balearn.database.chat.document

import com.jipsa.balearn.domain.chat.Chat
import com.jipsa.balearn.domain.chat.ChatId
import com.jipsa.balearn.domain.chat.ChatInfo
import com.jipsa.balearn.domain.chat.ChatType
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime

@Document(indexName = "chat")
class ChatDocument(
    @Id
    @Field(type = FieldType.Long)
    val id: Long,

    @Field(type = FieldType.Long)
    val teamId: Long,

    @Field(type = FieldType.Object)
    val sender: SenderVO,

    @Field(type = FieldType.Date)
    val createdAt: LocalDateTime,

    @Field(type = FieldType.Date)
    val modifiedAt: LocalDateTime,

    @Field(type = FieldType.Text)
    val message: String,

    @Field(type = FieldType.Keyword)
    val type: ChatType
) {
    fun toDomain(): Chat {
        return Chat(
            id = ChatId(id),
            teamId = TeamId(teamId),
            sender = sender.toDomain(),
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            _chatInfo = ChatInfo(message, type)
        )
    }

    companion object {
        fun from(chat: Chat): ChatDocument {
            return ChatDocument(
                id = chat.id.value,
                teamId = chat.teamId.value,
                sender = SenderVO.from(chat.sender),
                createdAt = chat.createdAt ?: LocalDateTime.now(),
                modifiedAt = chat.modifiedAt ?: LocalDateTime.now(),
                message = chat.chatInfo.message,
                type = chat.chatInfo.type
            )
        }
    }
}