package com.jipsa.balearn.domain.mission

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.schedule.Schedule
import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

class Mission(
    val id: MissionId = MissionId(),
    val schedule: Schedule,
    private var _missionInfo: MissionInfo,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    createdBy: UserId? = null,
    modifiedBy: UserId? = null
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    createdBy = createdBy,
    modifiedBy = modifiedBy
) {
    val missionInfo: MissionInfo
        get() = _missionInfo

    fun updateMissionInfo(missionInfo: MissionInfo) {
        _missionInfo = missionInfo
    }
}