package com.jipsa.balearn.api.notice.dto

import com.jipsa.balearn.api.team_user.dto.TeamUserReadResponse
import com.jipsa.balearn.domain.notice.Notice
import com.jipsa.balearn.domain.team_user.TeamUser
import java.time.LocalDateTime

data class NoticeResponse(
    val id: Long,
    val title: String,
    val detail: String,
    val createdAt: LocalDateTime?,
    val createdBy: TeamUserReadResponse?,
    val modifiedAt: LocalDateTime?,
    val modifiedBy: TeamUserReadResponse?
) {
    companion object {
        fun from(notice: Notice, createdBy: TeamUser?, modifiedBy: TeamUser?): NoticeResponse {
            return NoticeResponse(
                id = notice.id.value,
                title = notice.noticeInfo.title,
                detail = notice.noticeInfo.detail,
                createdAt = notice.createdAt,
                createdBy = createdBy?.let { TeamUserReadResponse.from(it) },
                modifiedAt = notice.modifiedAt,
                modifiedBy = modifiedBy?.let { TeamUserReadResponse.from(it) }
            )
        }
    }
}