package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NoticeRepository {
    fun save(notice: Notice): Notice
    fun findById(noticeId: NoticeId): Notice?
    fun findByTeamId(teamId: TeamId): List<Notice>
    fun delete(notice: Notice)
    fun deleteById(noticeId: NoticeId)
    fun findFirstByTeamId(teamId: TeamId): Notice?
    fun findByTeamId(teamId: TeamId, pageable: Pageable): Page<Notice>
    fun deleteByTeamId(teamId: TeamId)
}