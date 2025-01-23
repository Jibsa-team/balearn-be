package com.jipsa.balearn.domain.mission_clear

import com.jipsa.balearn.domain.mission.MissionId
import com.jipsa.balearn.domain.team_user.TeamUserId
import org.springframework.stereotype.Component

@Component
class MissionClearReader(
    private val missionClearRepository: MissionClearRepository
) {
    fun existsBy(missionId: MissionId, teamUserId: TeamUserId): Boolean {
        return missionClearRepository.existsById(missionId, teamUserId)
    }
}