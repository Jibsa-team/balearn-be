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

    fun updateScheduleInfo(
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        address: String?,
        topic: String?,
        color: String?
    ) {
        _scheduleInfo = ScheduleInfo(
            startTime = startTime ?: _scheduleInfo.startTime,
            endTime = endTime ?: _scheduleInfo.endTime,
            address = address ?: _scheduleInfo.address,
            topic = topic ?: _scheduleInfo.topic,
            color = color ?: _scheduleInfo.color
        )
    }

    fun validateScheduleTime() {
        if (_scheduleInfo.startTime.isAfter(_scheduleInfo.endTime)) {
            throw CustomScheduleException.ScheduleTimeInvalidException
        }
    }

    fun isCreator(userId: UserId) {
        require(createdBy == userId) { "작성자만 가능합니다." }
    }
}