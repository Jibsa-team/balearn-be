package com.jipsa.balearn.database.chat.document

import com.jipsa.balearn.domain.chat.Sender
import com.jipsa.balearn.domain.team_user.TeamUserId
import com.jipsa.balearn.domain.team_user.TeamUserRole
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

data class SenderVO(
    @Field(type = FieldType.Long)
    val id: Long,

    @Field(type = FieldType.Text)
    val nickname: String,

    @Field(type = FieldType.Text)
    val profileImageUrl: String,

    @Field(type = FieldType.Keyword)
    val role: TeamUserRole
) {
    fun toDomain(): Sender {
        return Sender(
            id = TeamUserId(id),
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            role = role
        )
    }

    companion object {
        fun from(sender: Sender): SenderVO {
            return SenderVO(
                id = sender.id.value,
                nickname = sender.nickname,
                profileImageUrl = sender.profileImageUrl,
                role = sender.role
            )
        }
    }
}