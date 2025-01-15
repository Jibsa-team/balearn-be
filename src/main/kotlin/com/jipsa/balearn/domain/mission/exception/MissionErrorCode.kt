package com.jipsa.balearn.domain.notice.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class MissionErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    MISSION_NOT_FOUND(404, "M001", "해당 미션을 찾지 못했습니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
