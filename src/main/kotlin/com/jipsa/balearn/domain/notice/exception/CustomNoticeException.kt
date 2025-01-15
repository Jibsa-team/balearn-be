package com.jipsa.balearn.domain.notice.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomNoticeException(errorCode: NoticeErrorCode) : CustomException(errorCode) {
    data object NoticeNotFoundException :
        CustomNoticeException(NoticeErrorCode.NOTICE_NOT_FOUND) {
        private fun readResolve(): Any = NoticeNotFoundException

        val EXCEPTION: CustomNoticeException = NoticeNotFoundException
    }
}