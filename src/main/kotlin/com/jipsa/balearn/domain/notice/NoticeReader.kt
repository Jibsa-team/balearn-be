package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.notice.exception.CustomNoticeException
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component

@Component
class NoticeReader(
    private val noticeRepository: NoticeRepository
) {
    fun read(noticeId: NoticeId): Notice {
        return noticeRepository.findById(noticeId) ?: throw CustomNoticeException.NoticeNotFoundException
    }

    fun readFirstBy(teamId: TeamId): Notice? {
        return noticeRepository.findFirstByTeamIdOrderByCreatedAtDesc(teamId)
    }
}