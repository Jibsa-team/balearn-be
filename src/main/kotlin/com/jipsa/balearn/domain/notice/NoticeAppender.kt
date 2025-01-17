package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.team.Team
import org.springframework.stereotype.Component

@Component
class NoticeAppender(
    private val noticeRepository: NoticeRepository,
) {

    fun append(notice: Notice, team: Team): Notice {
        return noticeRepository.save(notice)
    }
}