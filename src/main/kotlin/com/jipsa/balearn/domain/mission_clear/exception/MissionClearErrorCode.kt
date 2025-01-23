package com.jipsa.balearn.domain.schedule.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class MissionClearErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    ALREADY_MISSION_CLEAR(400, "MC001", "이미 미션을 클리어했습니다."),
    ALREADY_MISSION_NOT_CLEAR(400, "MC002", "미션 클리어 상태가 아닙니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
