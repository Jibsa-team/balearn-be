package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.api.mission_clear.dto.LeaderboardResponse
import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.mission.MissionReader
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.team_user.TeamUserReader
import com.jipsa.balearn.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MissionClearService(
    private val missionClearAppender: MissionClearAppender,
    private val missionReader: MissionReader,
    private val missionClearDeleter: MissionClearDeleter,
    private val teamUserReader: TeamUserReader,
    private val leaderboardReader: LeaderboardReader
) {
    @Transactional
    fun appendMissionClear(teamId: TeamId, user: User, missionId: MissionId): MissionClear {
        val mission = missionReader.read(missionId)
        val teamUser = teamUserReader.readBy(teamId, user.id)
        val missionClear = MissionClear(
            missionClearId = MissionClearId(missionId, teamUser.id),
            mission = mission,
            teamUser = teamUser
        )
        return missionClearAppender.append(missionClear)
    }

    @Transactional
    fun deleteMissionClear(teamId: TeamId, user: User, missionId: MissionId) {
        val teamUser = teamUserReader.readBy(teamId, user.id)
        missionClearDeleter.delete(teamUser, missionId)
    }

    @Transactional
    fun appendMissionClears(teamId: TeamId, user: User, createIds: List<MissionId>?, deleteIds: List<MissionId>?) {
        val teamUser = teamUserReader.readBy(teamId, user.id)

        createIds?.let {
            missionClearAppender.appendAll(it.map { missionId ->
                val mission = missionReader.read(missionId)
                MissionClear(
                    missionClearId = MissionClearId(missionId, teamUser.id),
                    mission = mission,
                    teamUser = teamUser
                )
            })
        }

        deleteIds?.let { missionClearDeleter.deleteAll(teamUser, it) }
    }


    fun readLeaderboard(teamId: TeamId, topN: Int): List<LeaderboardResponse> {
        return leaderboardReader.read(teamId, topN)
    }

    fun readMyRank(teamId: TeamId, user: User): LeaderboardResponse {
        val teamUser = teamUserReader.readBy(teamId, user.id)
        return leaderboardReader.read(teamUser)
    }
}