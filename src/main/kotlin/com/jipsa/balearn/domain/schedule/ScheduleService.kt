package com.jipsa.balearn.domain.schedule

import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.mission.MissionAppender
import com.jipsa.balearn.domain.mission.MissionInfo
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamReader
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ScheduleService(
    private val scheduleAppender: ScheduleAppender,
    private val missionAppender: MissionAppender,
    private val teamUserValidator: TeamUserValidator,
    private val teamReader: TeamReader
) {
    @Transactional
    fun appendSchedule(
        address: String,
        time: LocalDateTime,
        topic: String,
        missionInfos: List<MissionInfo>,
        userId: UserId,
        teamId: TeamId
    ): Schedule {
        val team = teamReader.read(teamId)

        teamUserValidator.validTeamUser(teamId, userId)

        val schedule = Schedule(
            _scheduleInfo = ScheduleInfo(
                address = address,
                time = time,
                topic = topic
            ),
            team = team
        )

        val newSchedule = scheduleAppender.append(schedule)

        missionAppender.appendAll(missionInfos.map {
            Mission(
                _missionInfo = it,
                schedule = newSchedule
            )
        })

        return newSchedule
    }
}