package com.jipsa.balearn.domain.notice.exception

import com.grepp.quizy.common.exception.BaseErrorCode
import com.grepp.quizy.common.exception.ErrorReason


enum class LearningFileErrorCode(
    private val status: Int,
    private val errorCode: String,
    private val message: String,
) : BaseErrorCode {
    LEARNING_FILE_NOT_FOUND(404, "LEARNING_FILE_001", "해당 학습 파일을 찾을 수 없습니다."),
    ;

    override val errorReason: ErrorReason
        get() = ErrorReason(status, errorCode, message)
}
