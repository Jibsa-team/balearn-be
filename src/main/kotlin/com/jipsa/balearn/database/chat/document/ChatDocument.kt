package com.jipsa.balearn.database.chat.document

import com.fasterxml.jackson.annotation.JsonFormat
import com.jipsa.balearn.domain.chat.Chat
import com.jipsa.balearn.domain.chat.ChatId
import com.jipsa.balearn.domain.chat.ChatInfo
import com.jipsa.balearn.domain.chat.ChatType
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Document(indexName = "chat")
@Setting(settingPath = "elasticsearch/chat-settings.json")
@Mapping(mappingPath = "elasticsearch/chat-mappings.json")
class ChatDocument(
    @Id
    @Field(type = FieldType.Keyword)
    val id: String,

    @Field(type = FieldType.Long)
    val teamId: Long,

    @Field(type = FieldType.Object)
    val sender: SenderVO,

    @Field(
        type = FieldType.Date,
        pattern = ["uuuu-MM-dd'T'HH:mm:ss.SSSSSS"],
        format = [DateFormat.strict_date_hour_minute_second_millis, DateFormat.epoch_millis]
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val createdAt: LocalDateTime,

    @Field(
        type = FieldType.Date,
        pattern = ["uuuu-MM-dd'T'HH:mm:ss.SSSSSS"],
        format = [DateFormat.strict_date_hour_minute_second_millis, DateFormat.epoch_millis]
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    val modifiedAt: LocalDateTime,

    @Field(type = FieldType.Text, analyzer = "standard")
    val message: String,

    @Field(type = FieldType.Keyword)
    val messageKeyword: String = message,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
            chatInfo = ChatInfo(message, type)
        )
    }

    companion object {
        fun from(chat: Chat): ChatDocument {
            val now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
            return ChatDocument(
                id = chat.id.value,
                teamId = chat.teamId.value,
                sender = SenderVO.from(chat.sender),
                createdAt = chat.createdAt ?: now,
                modifiedAt = chat.modifiedAt ?: now,
                message = chat.chatInfo.message,
                type = chat.chatInfo.type
            )
        }
    }
}