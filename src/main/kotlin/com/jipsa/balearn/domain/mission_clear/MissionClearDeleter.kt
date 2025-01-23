package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.team_user.TeamUserId
import org.springframework.stereotype.Component

@Component
class MissionClearDeleter(
    private val missionClearRepository: MissionClearRepository
) {
    fun delete(teamUserId: TeamUserId, missionId: MissionId) {
        missionClearRepository.deleteById(missionId, teamUserId)
    }
}