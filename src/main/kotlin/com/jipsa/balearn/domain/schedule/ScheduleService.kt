package com.jipsa.balearn.domain.schedule

import com.jipsa.balearn.api.schedule.dto.ScheduleResponse
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.mission.MissionAppender
import com.jipsa.balearn.domain.mission.MissionInfo
import com.jipsa.balearn.domain.mission.MissionReader
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
    private val scheduleReader: ScheduleReader,
    private val missionReader: MissionReader,
    private val teamUserValidator: TeamUserValidator,
    private val teamReader: TeamReader
) {
    @Transactional
    fun appendSchedule(
        address: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
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
                startTime = startTime,
                endTime = endTime,
                topic = topic
            ),
            team = team
        )

        scheduleReader.isExistBy(teamId, startTime, endTime)

        val newSchedule = scheduleAppender.append(schedule)

        missionAppender.appendAll(missionInfos.map {
            Mission(
                _missionInfo = it,
                schedule = newSchedule
            )
        })

        return newSchedule
    }

    fun readSchedule(scheduleId: ScheduleId, userId: UserId): ScheduleResponse {
        val schedule = scheduleReader.read(scheduleId)

        teamUserValidator.validTeamUser(schedule.team.id, userId)

        val missions = missionReader.readBy(scheduleId)

        return ScheduleResponse.from(schedule, missions)
    }

    fun readMonthlySchedules(year: Int, month: Int, userId: UserId, teamId: TeamId): List<Schedule> {
        teamUserValidator.validTeamUser(teamId, userId)

        return scheduleReader.readMonthlyScheduleBy(teamId, year, month)
    }

    fun readMonthlySchedules(userId: UserId, teamId: TeamId): List<Schedule> {
        teamUserValidator.validTeamUser(teamId, userId)

        return scheduleReader.readMonthlyScheduleBy(teamId)
    }

    fun readWeeklySchedules(userId: UserId, teamId: TeamId): List<Schedule> {
        teamUserValidator.validTeamUser(teamId, userId)

        return scheduleReader.readWeeklyScheduleBy(teamId)
    }

    fun readWeeklySchedules(year: Int, week: Int, userId: UserId, teamId: TeamId): List<Schedule> {
        teamUserValidator.validTeamUser(teamId, userId)

        return scheduleReader.readWeeklyScheduleBy(teamId, year, week)
    }
}