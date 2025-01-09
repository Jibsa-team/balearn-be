package com.jipsa.balearn.infra.oauth2.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomOAuth2Exception(errorCode: OAuth2ErrorCode) : CustomException(errorCode) {
    data object UnsupportedProviderException :
        CustomOAuth2Exception(OAuth2ErrorCode.UNSUPPORTED_PROVIDER) {
        private fun readResolve(): Any = UnsupportedProviderException

        val EXCEPTION: CustomOAuth2Exception = UnsupportedProviderException
    }

    data object DuplicateEmailException :
        CustomOAuth2Exception(OAuth2ErrorCode.DUPLICATED_EMAIL) {
        private fun readResolve(): Any = DuplicateEmailException

        val EXCEPTION: CustomOAuth2Exception = DuplicateEmailException
    }
}