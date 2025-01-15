package com.jipsa.balearn.domain.schedule.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomScheduleException(errorCode: ScheduleErrorCode) : CustomException(errorCode) {
    data object ScheduleNotFoundException :
        CustomScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND) {
        private fun readResolve(): Any = ScheduleNotFoundException

        val EXCEPTION: CustomScheduleException = ScheduleNotFoundException
    }
}