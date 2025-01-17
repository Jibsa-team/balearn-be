package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.schedule.exception.CustomTeamException
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamReader
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoticeService(
    private val noticeAppender: NoticeAppender,
    private val noticeReader: NoticeReader,
    private val teamUserValidator: TeamUserValidator,
    private val teamReader: TeamReader

) {
    @Transactional
    fun appendNotice(title: String, detail: String, userId: UserId, teamId: TeamId): Notice {
        val team = teamReader.read(teamId)

        teamUserValidator.validLeader(teamId, userId)

        val notice = Notice(
            _noticeInfo = NoticeInfo(
                title = title,
                detail = detail
            ),
            team = team
        )

        return noticeAppender.append(notice, team)
    }

    fun readNotice(userId: UserId, noticeId: NoticeId): Notice {
        return noticeReader.read(userId, noticeId)
    }
}