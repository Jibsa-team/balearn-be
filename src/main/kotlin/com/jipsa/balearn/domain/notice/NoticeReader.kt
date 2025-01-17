package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.notice.exception.CustomNoticeException
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Component

@Component
class NoticeReader(
    private val noticeRepository: NoticeRepository,
    private val teamUserValidator: TeamUserValidator,
) {
    fun read(userId: UserId, noticeId: NoticeId): Notice {
        val notice = noticeRepository.findById(noticeId)
            ?: throw CustomNoticeException.NoticeNotFoundException

        teamUserValidator.validTeamUser(notice.team.id, userId)

        return notice
    }

    fun readFirstBy(teamId: TeamId): Notice? {
        return noticeRepository.findFirstByTeamIdOrderByCreatedAtDesc(teamId)
    }
}