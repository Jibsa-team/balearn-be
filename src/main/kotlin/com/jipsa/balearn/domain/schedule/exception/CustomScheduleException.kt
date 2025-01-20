package com.jipsa.balearn.domain.schedule.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomScheduleException(errorCode: ScheduleErrorCode) : CustomException(errorCode) {
    data object ScheduleNotFoundException :
        CustomScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND) {
        private fun readResolve(): Any = ScheduleNotFoundException

        val EXCEPTION: CustomScheduleException = ScheduleNotFoundException
    }

    data object ScheduleTimeInvalidException :
        CustomScheduleException(ScheduleErrorCode.SCHEDULE_TIME_INVALID) {
        private fun readResolve(): Any = ScheduleTimeInvalidException

        val EXCEPTION: CustomScheduleException = ScheduleTimeInvalidException
    }

    data object ScheduleTimeOverlapException :
        CustomScheduleException(ScheduleErrorCode.SCHEDULE_TIME_OVERLAP) {
        private fun readResolve(): Any = ScheduleTimeOverlapException

        val EXCEPTION: CustomScheduleException = ScheduleTimeOverlapException
    }
}