package com.jipsa.balearn.domain.schedule.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomTeamException(errorCode: TeamErrorCode) : CustomException(errorCode) {
    data object TeamNotFoundException :
        CustomTeamException(TeamErrorCode.TEAM_NOT_FOUND) {
        private fun readResolve(): Any = TeamNotFoundException

        val EXCEPTION: CustomTeamException = TeamNotFoundException
    }
}