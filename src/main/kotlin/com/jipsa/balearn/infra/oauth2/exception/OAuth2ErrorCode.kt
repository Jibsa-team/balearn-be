package com.jipsa.balearn.infra.oauth2.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class OAuth2ErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    UNSUPPORTED_PROVIDER(403, "O001", "지원하지 않는 OAuth2 제공자입니다."),
    DUPLICATED_EMAIL(409, "O002", "이미 가입된 이메일입니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
