package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component

@Component
class NoticeDeleter(
    private val noticeRepository: NoticeRepository
) {
    fun delete(notice: Notice) {
        noticeRepository.delete(notice)
    }

    fun delete(noticeId: NoticeId) {
        noticeRepository.deleteById(noticeId)
    }

    fun deleteBy(teamId: TeamId) {
        noticeRepository.deleteByTeamId(teamId)
    }
}