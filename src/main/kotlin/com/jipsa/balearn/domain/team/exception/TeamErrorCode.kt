package com.jipsa.balearn.domain.schedule.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class TeamErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    TEAM_NOT_FOUND(404, "T001", "해당 모임을 찾지 못했습니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
