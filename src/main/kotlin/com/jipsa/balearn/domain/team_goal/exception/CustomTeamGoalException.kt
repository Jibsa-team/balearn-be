package com.jipsa.balearn.domain.schedule.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomTeamGoalException(errorCode: TeamGoalErrorCode) : CustomException(errorCode) {
    data object TeamGoalNotFoundException :
        CustomTeamGoalException(TeamGoalErrorCode.TEAM_GOAL_NOT_FOUND) {
        private fun readResolve(): Any = TeamGoalNotFoundException

        val EXCEPTION: CustomTeamGoalException = TeamGoalNotFoundException
    }
}