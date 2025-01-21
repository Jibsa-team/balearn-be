package com.jipsa.balearn.domain.notice

import org.springframework.stereotype.Component

@Component
class NoticeUpdater(
    private val noticeRepository: NoticeRepository
) {
    fun update(notice: Notice, title: String?, detail: String?): Notice {
        notice.updateNoticeInfo(title, detail)

        return noticeRepository.save(notice)
    }
}