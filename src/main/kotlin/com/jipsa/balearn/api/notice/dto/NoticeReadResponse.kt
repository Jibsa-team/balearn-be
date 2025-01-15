package com.jipsa.balearn.api.notice.dto

import com.jipsa.balearn.domain.notice.Notice
import java.time.LocalDateTime

data class NoticeReadResponse(
    val id: Long,
    val title: String,
    val detail: String,
    val createdAt: LocalDateTime?,
    val createdBy: Long?,
    val modifiedAt: LocalDateTime?,
    val modifiedBy: Long?
) {
    companion object {
        fun from(notice: Notice): NoticeReadResponse {
            return NoticeReadResponse(
                id = notice.id.value,
                title = notice.noticeInfo.title,
                detail = notice.noticeInfo.detail,
                createdAt = notice.createdAt,
                createdBy = notice.createdBy?.value,
                modifiedAt = notice.modifiedAt,
                modifiedBy = notice.modifiedBy?.value
            )
        }
    }
}