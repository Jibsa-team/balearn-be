package com.jipsa.balearn.api.user.dto

import com.jipsa.balearn.domain.user.ReissueToken

data class TokenResponse(
    val accessToken: String,
    val expirationTime: Long
) {
    companion object {
        fun from(reissueToken: ReissueToken): TokenResponse {
            return TokenResponse(
                accessToken = reissueToken.accessToken,
                expirationTime = reissueToken.accessTokenExpirationTime
            )
        }
    }
}