package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

class Notice(
    val id: NoticeId,
    val team: Team,
    private var _noticeInfo: NoticeInfo,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    createdBy: UserId? = null,
    modifiedBy: UserId? = null
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    createdBy = createdBy,
    modifiedBy = modifiedBy
) {
    val noticeInfo: NoticeInfo
        get() = _noticeInfo

    fun updateNoticeInfo(noticeInfo: NoticeInfo) {
        this._noticeInfo = noticeInfo
    }

    fun isOwner(userId: UserId) {
        require(this.createdBy == userId) { "작성자만 가능합니다." }
    }
}