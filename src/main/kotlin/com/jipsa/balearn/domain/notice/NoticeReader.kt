package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.notice.exception.CustomNoticeException
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class NoticeReader(
    private val noticeRepository: NoticeRepository,
) {
    fun read(noticeId: NoticeId): Notice {
        return noticeRepository.findById(noticeId)
            ?: throw CustomNoticeException.NoticeNotFoundException
    }

    fun readFirstBy(teamId: TeamId): Notice? {
        return noticeRepository.findFirstByTeamId(teamId)
    }

    fun readBy(teamId: TeamId, pageable: Pageable): Page<Notice> {
        return noticeRepository.findByTeamId(teamId, pageable)
    }
}