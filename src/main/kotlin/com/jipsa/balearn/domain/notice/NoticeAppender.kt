package com.jipsa.balearn.domain.notice

import com.jipsa.balearn.domain.schedule.exception.CustomTeamException
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamRepository
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Component

@Component
class NoticeAppender(
    private val noticeRepository: NoticeRepository,
    private val teamRepository: TeamRepository,
    private val teamUserValidator: TeamUserValidator,
) {

    fun append(title: String, detail: String, userId: UserId, teamId: TeamId): Notice {
        val team = teamRepository.findById(teamId)
            ?: throw CustomTeamException.TeamNotFoundException

        teamUserValidator.validLeader(teamId, userId)

        val notice = Notice(
            _noticeInfo = NoticeInfo(
                title = title,
                detail = detail
            ),
            team = team
        )

        return noticeRepository.save(notice)
    }


}