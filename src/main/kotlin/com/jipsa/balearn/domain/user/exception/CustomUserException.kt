package com.jipsa.balearn.domain.user.exception

import com.grepp.quizy.common.exception.CustomException

sealed class CustomUserException(errorCode: UserErrorCode) : CustomException(errorCode) {
    data object UserNotFoundException :
        CustomUserException(UserErrorCode.USER_NOT_FOUND) {
        private fun readResolve(): Any = UserNotFoundException

        val EXCEPTION: CustomUserException = UserNotFoundException
    }

    data object UserNotAuthenticatedException :
        CustomUserException(UserErrorCode.USER_NOT_AUTHENTICATED) {
        private fun readResolve(): Any = UserNotAuthenticatedException

        val EXCEPTION: CustomUserException = UserNotAuthenticatedException
    }
}