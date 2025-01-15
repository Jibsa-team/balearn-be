package com.jipsa.balearn.domain.team

import com.jipsa.balearn.api.notice.dto.NoticeReadResponse
import com.jipsa.balearn.api.schedule.dto.ScheduleReadResponse
import com.jipsa.balearn.api.team.dto.TeamReadResponse
import com.jipsa.balearn.api.team.dto.TeamResponse
import com.jipsa.balearn.api.team_goal.dto.TeamGoalReadResponse
import com.jipsa.balearn.api.team_user.dto.TeamUserReadResponse
import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.mission.MissionReader
import com.jipsa.balearn.domain.notice.NoticeReader
import com.jipsa.balearn.domain.schedule.ScheduleReader
import com.jipsa.balearn.domain.team_goal.TeamGoal
import com.jipsa.balearn.domain.team_goal.TeamGoalAppender
import com.jipsa.balearn.domain.team_goal.TeamGoalInfo
import com.jipsa.balearn.domain.team_goal.TeamGoalReader
import com.jipsa.balearn.domain.team_user.*
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamService(
    private val teamAppender: TeamAppender,
    private val teamReader: TeamReader,
    private val teamGoalAppender: TeamGoalAppender,
    private val teamUserAppender: TeamUserAppender,
    private val teamImageAppender: TeamImageAppender,
    private val teamGoalReader: TeamGoalReader,
    private val teamUserReader: TeamUserReader,
    private val scheduleReader: ScheduleReader,
    private val missionReader: MissionReader,
    private val noticeReader: NoticeReader,
    private val teamInviter: TeamInviter,
    private val teamUserValidator: TeamUserValidator
) {
    @Transactional
    fun createTeam(name: String, description: String, goals: List<TeamGoalInfo>?, image: File?, user: User): Team {

        val teamInfo = teamImageAppender.append(image)?.let {
            TeamInfo(
                name = name,
                description = description,
                teamImageUrl = it
            )
        } ?: TeamInfo(
            name = name,
            description = description
        )

        val team = teamAppender.append(
            Team(
                _teamInfo = teamInfo
            )
        )

        goals?.let { goalInfos ->
            teamGoalAppender.appendAll(goalInfos.map {
                TeamGoal(
                    team = team,
                    _teamGoalInfo = it
                )
            })
        }

        teamUserAppender.append(
            TeamUser.from(
                team = team,
                user = user,
                role = TeamUserRole.OWNER
            )
        )

        return team
    }

    @Transactional(readOnly = true)
    fun readTeam(teamId: TeamId, userId: UserId): TeamResponse {
        teamUserValidator.validTeamUser(teamId, userId)
        val team = TeamReadResponse.from(teamReader.read(teamId))
        val goals = teamGoalReader.readBy(teamId).map { TeamGoalReadResponse.from(it) }
        val users = teamUserReader.readBy(teamId).map { TeamUserReadResponse.from(it) }
        val schedules = scheduleReader.readWeeklyScheduleBy(teamId)
            .map { ScheduleReadResponse.from(it, missionReader.readBy(it.id)) }
        val notice = noticeReader.readFirstBy(teamId)?.let { NoticeReadResponse.from(it) }

        return TeamResponse(
            team = team,
            goal = goals,
            teamUser = users,
            weeklySchedule = schedules,
            notice = notice
        )
    }

    @Transactional(readOnly = true)
    fun readMyTeams(userId: UserId): List<Team> {
        return teamUserReader.readBy(userId).let { teamUsers -> teamUsers.map { it.team } }
    }

    @Transactional
    fun invite(teamId: TeamId, userId: UserId): String {
        return teamInviter.inviteUser(teamId, userId)
    }

    @Transactional
    fun joinTeam(inviteCode: String, user: User): TeamUser {
        return teamUserAppender.join(inviteCode, user)
    }
}