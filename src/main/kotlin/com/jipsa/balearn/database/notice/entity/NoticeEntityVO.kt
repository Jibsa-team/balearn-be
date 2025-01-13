package com.jipsa.balearn.database.notice.entity

import com.jipsa.balearn.domain.notice.NoticeInfo
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class NoticeInfoVO(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val detail: String,
) {
    fun toDomain() = NoticeInfo(
        title = title,
        detail = detail
    )

    companion object {
        fun from(noticeInfo: NoticeInfo) = NoticeInfoVO(
            title = noticeInfo.title,
            detail = noticeInfo.detail
        )
    }
}