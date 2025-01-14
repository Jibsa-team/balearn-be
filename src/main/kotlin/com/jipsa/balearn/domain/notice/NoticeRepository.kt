package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.team.TeamId

interface NoticeRepository {
    fun save(notice: Notice): Notice
    fun findById(noticeId: NoticeId): Notice?
    fun findByTeamId(teamId: TeamId): List<Notice>
    fun delete(notice: Notice)
    fun deleteById(noticeId: NoticeId)
}