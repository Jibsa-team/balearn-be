package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.api.notice.dto.NoticeResponse
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserReader
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeService(
    private val noticeAppender: NoticeAppender,
    private val noticeReader: NoticeReader,
    private val teamUserValidator: TeamUserValidator,
    private val noticeUpdater: NoticeUpdater,
    private val noticeDeleter: NoticeDeleter,
    private val teamUserReader: TeamUserReader
) {
    @Transactional
    fun appendNotice(title: String, detail: String, userId: UserId, teamId: TeamId): NoticeResponse {
        val teamUser = teamUserReader.readBy(teamId, userId)

        teamUserValidator.validLeader(teamId, userId)

        val notice = Notice(
            _noticeInfo = NoticeInfo(
                title = title,
                detail = detail
            ),
            team = teamUser.team
        )

        val newNotice = noticeAppender.append(notice, teamUser.team)

        return NoticeResponse.from(newNotice, teamUser, teamUser)
    }

    fun readNotice(userId: UserId, noticeId: NoticeId): NoticeResponse {
        val notice = noticeReader.read(noticeId)

        teamUserValidator.validTeamUser(notice.team.id, userId)

        val createdBy = notice.createdBy?.let { teamUserReader.readBy(notice.team.id, it) }
        val modifiedBy = notice.modifiedBy?.let { teamUserReader.readBy(notice.team.id, it) }

        return NoticeResponse.from(notice, createdBy, modifiedBy)
    }

    fun readNoticePage(userId: UserId, teamId: TeamId, pageable: Pageable): Page<NoticeResponse> {
        teamUserValidator.validTeamUser(teamId, userId)

        return noticeReader.readBy(teamId, pageable).map { notice ->
            val createdBy = notice.createdBy?.let { teamUserReader.readBy(notice.team.id, it) }
            val modifiedBy = notice.modifiedBy?.let { teamUserReader.readBy(notice.team.id, it) }

            NoticeResponse.from(notice, createdBy, modifiedBy)
        }
    }

    @Transactional
    fun updateNotice(user: User, noticeId: NoticeId, title: String?, detail: String?): NoticeResponse {
        val notice = noticeReader.read(noticeId)

        try {
            teamUserValidator.validOwner(notice.team.id, user.id)
        } catch (e: Exception) {
            teamUserValidator.validLeader(notice.team.id, user.id)
            notice.isCreator(user.id)
        }

        val updatedNotice = noticeUpdater.update(notice, title, detail)
        val createdBy = updatedNotice.createdBy?.let { teamUserReader.readBy(updatedNotice.team.id, it) }
        val modifiedBy = updatedNotice.modifiedBy?.let { teamUserReader.readBy(updatedNotice.team.id, it) }

        return NoticeResponse.from(updatedNotice, createdBy, modifiedBy)
    }

    @Transactional
    fun deleteNotice(user: User, noticeId: NoticeId) {
        val notice = noticeReader.read(noticeId)

        try {
            teamUserValidator.validOwner(notice.team.id, user.id)
        } catch (e: Exception) {
            teamUserValidator.validLeader(notice.team.id, user.id)
            notice.isCreator(user.id)
        }

        noticeDeleter.delete(notice)
    }
}