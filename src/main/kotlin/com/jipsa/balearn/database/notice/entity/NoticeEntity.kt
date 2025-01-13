package com.jipsa.balearn.database.notice.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.domain.notice.Notice
import com.jipsa.balearn.domain.notice.NoticeId
import com.jipsa.balearn.domain.user.UserId
import jakarta.persistence.*

@Entity
@Table(name = "notices")
class NoticeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: TeamEntity,

    @Embedded
    val noticeInfo: NoticeInfoVO,
) : BaseEntity() {

    fun toDomain() = Notice(
        id = NoticeId(id),
        team = team.toDomain(),
        _noticeInfo = noticeInfo.toDomain(),
        createdAt = createdAt,
        modifiedAt = modifiedAt,
        createdBy = createdBy?.let { UserId(it) },
        modifiedBy = modifiedBy?.let { UserId(it) }
    )

    companion object {
        fun from(notice: Notice) = NoticeEntity(
            id = notice.id.value,
            team = TeamEntity.from(notice.team),
            noticeInfo = NoticeInfoVO.from(notice.noticeInfo)
        )
    }
}