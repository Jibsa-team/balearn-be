package com.jipsa.balearn.database.mission_clear.repository

import com.jipsa.balearn.database.mission_clear.entity.MissionClearEntity
import com.jipsa.balearn.database.mission_clear.entity.MissionClearEntityId
import com.jipsa.balearn.domain.mission.Mission
import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.mission_clear.MissionClear
import com.jipsa.balearn.domain.mission_clear.MissionClearId
import com.jipsa.balearn.domain.mission_clear.MissionClearRepository
import com.jipsa.balearn.domain.team_user.TeamUser
import com.jipsa.balearn.domain.team_user.TeamUserId
import org.springframework.stereotype.Repository

@Repository
class MissionClearRepositoryAdaptor(
    private val missionClearJpaRepository: MissionClearJpaRepository
) : MissionClearRepository {
    override fun save(missionClear: MissionClear): MissionClear {
        return missionClearJpaRepository.save(MissionClearEntity.from(missionClear)).toDomain()
    }

    override fun save(mission: Mission, teamUser: TeamUser): MissionClear {
        return missionClearJpaRepository.save(
            MissionClearEntity.from(
                MissionClear(
                    MissionClearId(
                        mission.id,
                        teamUser.id
                    ),
                    mission,
                    teamUser
                )
            )
        ).toDomain()
    }

    override fun existsById(missionClearId: MissionClearId): Boolean {
        return missionClearJpaRepository.existsById(MissionClearEntityId.from(missionClearId))
    }

    override fun existsById(missionId: MissionId, teamUserId: TeamUserId): Boolean {
        return missionClearJpaRepository.existsById(MissionClearEntityId(missionId, teamUserId))
    }

    override fun deleteById(missionClearId: MissionClearId) {
        missionClearJpaRepository.deleteById(MissionClearEntityId.from(missionClearId))
    }

    override fun deleteById(missionId: MissionId, teamUserId: TeamUserId) {
        missionClearJpaRepository.deleteById(MissionClearEntityId(missionId, teamUserId))
    }

    override fun deleteByMissionId(missionId: MissionId) {
        missionClearJpaRepository.deleteByMissionClearId_MissionId(missionId.value)
    }

    override fun deleteByTeamUserId(teamUserId: TeamUserId) {
        missionClearJpaRepository.deleteByMissionClearId_TeamUserId(teamUserId.value)
    }
}