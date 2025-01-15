package com.jipsa.balearn.database.notice.repository

import com.jipsa.balearn.database.notice.entity.NoticeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeJpaRepository : JpaRepository<NoticeEntity, Long> {
    fun findByTeam_Id(teamId: Long): List<NoticeEntity>

    fun findFirstByTeam_IdOrderByCreatedAtDesc(teamId: Long): NoticeEntity?
}