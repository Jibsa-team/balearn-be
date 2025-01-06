package com.jipsa.balearn.domain.user

data class ReissueToken(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpirationTime: Long,
)