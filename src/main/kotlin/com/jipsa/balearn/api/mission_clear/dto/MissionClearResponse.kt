package com.jipsa.balearn.api.mission_clear.dto

import com.jipsa.balearn.domain.mission_clear.MissionClear

data class MissionClearResponse(
    val missionId: Long,
    val teamUserId: Long,
) {
    companion object {
        fun from(missionClear: MissionClear): MissionClearResponse {
            return MissionClearResponse(
                missionId = missionClear.missionClearId.missionId.value,
                teamUserId = missionClear.missionClearId.teamUserId.value,
            )
        }
    }
}