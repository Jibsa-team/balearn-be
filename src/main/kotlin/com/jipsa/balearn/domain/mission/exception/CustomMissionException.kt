package com.jipsa.balearn.domain.notice.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomMissionException(errorCode: MissionErrorCode) : CustomException(errorCode) {
    data object MissionNotFoundException :
        CustomMissionException(MissionErrorCode.MISSION_NOT_FOUND) {
        private fun readResolve(): Any = MissionNotFoundException

        val EXCEPTION: CustomMissionException = MissionNotFoundException
    }
}