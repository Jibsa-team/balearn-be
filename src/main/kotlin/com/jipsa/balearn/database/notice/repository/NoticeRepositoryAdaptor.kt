package com.jipsa.balearn.database.notice.repository

import com.jipsa.balearn.database.notice.entity.NoticeEntity
import com.jipsa.balearn.domain.notice.Notice
import com.jipsa.balearn.domain.notice.NoticeId
import com.jipsa.balearn.domain.notice.NoticeRepository
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class NoticeRepositoryAdaptor(
    private val noticeJpaRepository: NoticeJpaRepository
) : NoticeRepository {
    override fun save(notice: Notice): Notice {
        return noticeJpaRepository.save(NoticeEntity.from(notice)).toDomain()
    }

    override fun findById(noticeId: NoticeId): Notice? {
        return noticeJpaRepository.findById(noticeId.value).getOrNull()?.toDomain()
    }

    override fun findByTeamId(teamId: TeamId): List<Notice> {
        return noticeJpaRepository.findByTeam_Id(teamId.value).map { it.toDomain() }
    }

    override fun findFirstByTeamIdOrderByCreatedAtDesc(teamId: TeamId): Notice? {
        return noticeJpaRepository.findFirstByTeam_IdOrderByCreatedAtDesc(teamId.value)?.toDomain()
    }

    override fun delete(notice: Notice) {
        noticeJpaRepository.delete(NoticeEntity.from(notice))
    }

    override fun deleteById(noticeId: NoticeId) {
        noticeJpaRepository.deleteById(noticeId.value)
    }
}