package com.jipsa.balearn.domain.team_user.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class TeamUserErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    TEAM_USER_NOT_FOUND(404, "TU001", "해당 모임원을 찾지 못했습니다."),
    TEAM_USER_ALREADY_EXIST(409, "TU002", "이미 해당 모임원이 존재합니다."),
    TEAM_USER_NOT_AUTHORIZED(403, "TU003", "해당 모임원에 대한 권한이 없습니다."),
    TEAM_USER_NOT_VALID(403, "TU004", "해당 모임에 가입되어있지 않습니다."),
    INVITE_CODE_NOT_VALID(403, "TU005", "해당 초대 코드가 유효하지 않습니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
