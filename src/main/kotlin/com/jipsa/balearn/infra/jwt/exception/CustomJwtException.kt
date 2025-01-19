package com.jipsa.balearn.infra.jwt.exception

import com.jipsa.balearn.common.exception.CustomException


sealed class CustomJwtException(errorCode: JwtErrorCode) :
    CustomException(errorCode) {

    data object JwtExpriedException :
        CustomJwtException(JwtErrorCode.TOKEN_EXPIRED) {
        private fun readResolve(): Any = JwtExpriedException

        val EXCEPTION: CustomJwtException = JwtExpriedException
    }

    data object JwtNotFountException :
        CustomJwtException(JwtErrorCode.TOKEN_NOT_FOUNT) {
        private fun readResolve(): Any = JwtNotFountException

        val EXCEPTION: CustomJwtException = JwtNotFountException
    }

    data object JwtNotValidateException :
        CustomJwtException(JwtErrorCode.TOKEN_NOT_VALIDATE) {
        private fun readResolve(): Any = JwtNotValidateException

        val EXCEPTION: CustomJwtException = JwtNotValidateException
    }

    data object JwtUnknownException :
        CustomJwtException(JwtErrorCode.UNKNOWN_EXCEPTION) {
        private fun readResolve(): Any = JwtUnknownException

        val EXCEPTION: CustomJwtException = JwtUnknownException
    }

    data object JwtUnsupportedException :
        CustomJwtException(JwtErrorCode.UNSUPPORTED_TOKEN) {
        private fun readResolve(): Any = JwtUnsupportedException

        val EXCEPTION: CustomJwtException = JwtUnsupportedException
    }

    data object JwtLoggedOutException :
        CustomJwtException(JwtErrorCode.LOGGED_OUT_USER) {
        private fun readResolve(): Any = JwtLoggedOutException

        val EXCEPTION: CustomJwtException = JwtLoggedOutException
    }

    data object NotExistUserException :
        CustomJwtException(JwtErrorCode.USER_NOT_FOUND) {
        private fun readResolve(): Any = NotExistUserException

        val EXCEPTION: CustomJwtException = NotExistUserException
    }
}
