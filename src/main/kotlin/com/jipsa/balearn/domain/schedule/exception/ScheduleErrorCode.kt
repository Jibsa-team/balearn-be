package com.jipsa.balearn.domain.schedule.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class ScheduleErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    SCHEDULE_NOT_FOUND(404, "S001", "해당 일정을 찾지 못했습니다."),
    SCHEDULE_TIME_INVALID(400, "S002", "시작 시간이 종료 시간보다 늦을 수 없습니다."),
    SCHEDULE_TIME_OVERLAP(400, "S003", "일정이 겹칩니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
