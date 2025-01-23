package com.jipsa.balearn.domain.schedule

import com.jipsa.balearn.api.mission.dto.MissionUpdateRequest
import com.jipsa.balearn.api.schedule.dto.ScheduleResponse
import com.jipsa.balearn.domain.mission.*
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team.TeamReader
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.domain.user.User
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
    private val teamReader: TeamReader,
    private val scheduleUpdater: ScheduleUpdater,
    private val missionUpdater: MissionUpdater,
    private val scheduleDeleter: ScheduleDeleter,
    private val missionDeleter: MissionDeleter
) {
    @Transactional
    fun appendSchedule(
        address: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        topic: String,
        color: String?,
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
                topic = topic,
                color = color ?: "#D3D3D3"
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

    @Transactional
    fun updateSchedule(
        scheduleId: ScheduleId,
        address: String?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        topic: String?,
        color: String?,
        missionUpdateRequest: List<MissionUpdateRequest>?,
        deleteMissionIds: List<MissionId>?,
        user: User,
    ): ScheduleResponse {
        val schedule = scheduleReader.read(scheduleId)

        teamUserValidator.validTeamUser(schedule.team.id, user.id)

        try {
            teamUserValidator.validLeader(schedule.team.id, user.id)
        } catch (e: Exception) {
            schedule.isCreator(user.id)
        }

        if (startTime != null || endTime != null) {
            if (startTime != schedule.scheduleInfo.startTime || endTime != schedule.scheduleInfo.endTime) {
                scheduleReader.isExistBy(
                    schedule.team.id,
                    startTime ?: schedule.scheduleInfo.startTime,
                    endTime ?: schedule.scheduleInfo.endTime
                )
            }
        }

        val newSchedule =
            scheduleUpdater.update(
                schedule = schedule,
                address = address,
                startTime = startTime,
                endTime = endTime,
                topic = topic,
                color = color
            )

        missionUpdateRequest?.let { missionUpdater.updateMissions(it, newSchedule) }
        deleteMissionIds?.let { missionDeleter.deleteAllBy(it) }

        val missions =
            missionReader.readBy(scheduleId)

        return ScheduleResponse.from(newSchedule, missions)
    }

    @Transactional
    fun deleteSchedule(scheduleId: ScheduleId, user: User) {
        val schedule = scheduleReader.read(scheduleId)

        teamUserValidator.validTeamUser(schedule.team.id, user.id)

        try {
            teamUserValidator.validLeader(schedule.team.id, user.id)
        } catch (e: Exception) {
            schedule.isCreator(user.id)
        }

        scheduleDeleter.delete(schedule)
    }
}