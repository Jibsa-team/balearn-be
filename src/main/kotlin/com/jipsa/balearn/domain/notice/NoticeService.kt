package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeAppender: NoticeAppender,
    private val noticeReader: NoticeReader
) {
    fun appendNotice(title: String, detail: String, userId: UserId, teamId: TeamId): Notice {
        return noticeAppender.append(title, detail, userId, teamId)
    }

    fun readNotice(userId: UserId, noticeId: NoticeId) {
        noticeReader.read(userId, noticeId)
    }
}