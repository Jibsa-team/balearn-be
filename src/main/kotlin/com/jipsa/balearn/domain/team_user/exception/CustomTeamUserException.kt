package com.jipsa.balearn.domain.team_user.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomTeamUserException(errorCode: TeamUserErrorCode) : CustomException(errorCode) {
    data object TeamUserNotFoundException :
        CustomTeamUserException(TeamUserErrorCode.TEAM_USER_NOT_FOUND) {
        private fun readResolve(): Any = TeamUserNotFoundException

        val EXCEPTION: CustomTeamUserException = TeamUserNotFoundException
    }

    data object TeamUserAlreadyExistException :
        CustomTeamUserException(TeamUserErrorCode.TEAM_USER_ALREADY_EXIST) {
        private fun readResolve(): Any = TeamUserAlreadyExistException

        val EXCEPTION: CustomTeamUserException = TeamUserAlreadyExistException
    }

    data object TeamUserNotAuthorizedException :
        CustomTeamUserException(TeamUserErrorCode.TEAM_USER_NOT_AUTHORIZED) {
        private fun readResolve(): Any = TeamUserNotAuthorizedException

        val EXCEPTION: CustomTeamUserException = TeamUserNotAuthorizedException
    }

    data object TeamUserNotValidException :
        CustomTeamUserException(TeamUserErrorCode.TEAM_USER_NOT_VALID) {
        private fun readResolve(): Any = TeamUserNotValidException

        val EXCEPTION: CustomTeamUserException = TeamUserNotValidException
    }

    data object InviteCodeNotValidException :
        CustomTeamUserException(TeamUserErrorCode.INVITE_CODE_NOT_VALID) {
        private fun readResolve(): Any = InviteCodeNotValidException

        val EXCEPTION: CustomTeamUserException = InviteCodeNotValidException
    }

    data object OwnerCannotLeaveException :
        CustomTeamUserException(TeamUserErrorCode.OWNER_CANNOT_LEAVE) {
        private fun readResolve(): Any = OwnerCannotLeaveException

        val EXCEPTION: CustomTeamUserException = OwnerCannotLeaveException
    }
}