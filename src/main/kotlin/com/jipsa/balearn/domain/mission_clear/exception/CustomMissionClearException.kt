package com.jipsa.balearn.domain.schedule.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomMissionClearException(errorCode: MissionClearErrorCode) : CustomException(errorCode) {
    data object AlreadyMissionClearException :
        CustomMissionClearException(MissionClearErrorCode.ALREADY_MISSION_CLEAR) {
        private fun readResolve(): Any = AlreadyMissionClearException

        val EXCEPTION: CustomMissionClearException = AlreadyMissionClearException
    }

    data object AlreadyMissionNotClearException :
        CustomMissionClearException(MissionClearErrorCode.ALREADY_MISSION_NOT_CLEAR) {
        private fun readResolve(): Any = AlreadyMissionNotClearException

        val EXCEPTION: CustomMissionClearException = AlreadyMissionNotClearException
    }
}