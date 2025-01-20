package com.jipsa.balearn.domain.schedule.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class TeamGoalErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    TEAM_GOAL_NOT_FOUND(404, "TG001", "해당 팀 목표를 찾지 못했습니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
