package com.jipsa.balearn.domain.schedule

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.schedule.exception.CustomScheduleException
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

class Schedule(
    val id: ScheduleId = ScheduleId(),
    val team: Team,
    private var _scheduleInfo: ScheduleInfo,
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
    val scheduleInfo: ScheduleInfo
        get() = _scheduleInfo

    fun updateScheduleInfo(scheduleInfo: ScheduleInfo) {
        _scheduleInfo = scheduleInfo
    }

    fun validateScheduleTime() {
        if (_scheduleInfo.startTime.isAfter(_scheduleInfo.endTime)) {
            throw CustomScheduleException.ScheduleTimeInvalidException
        }
    }
}