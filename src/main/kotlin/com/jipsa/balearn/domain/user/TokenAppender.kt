package com.jipsa.balearn.domain.user

import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.common.util.CookieUtil
import com.jipsa.balearn.infra.jwt.JwtProperties
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class TokenAppender(
    private val redisRepository: RedisRepository,
    private val jwtProperties: JwtProperties,
    private val cookieUtil: CookieUtil
) {
    fun appendRefreshToken(response: HttpServletResponse, userId: UserId, refreshToken: String) {
        redisRepository.saveValue(
            redisRepository.generateRefreshTokenKey(userId),
            refreshToken,
            jwtProperties.refreshTokenValidity
        )

        cookieUtil.addCookie(
            response = response,
            name = BalearnConstants.REFRESH_TOKEN,
            value = refreshToken
        )
    }

    fun appendLoginToken(response: HttpServletResponse, userId: UserId, loginToken: String) {
        redisRepository.saveValue(
            redisRepository.generateLoginTokenKey(userId),
            loginToken,
            jwtProperties.loginTokenValidity
        )

        cookieUtil.addCookie(
            response = response,
            name = BalearnConstants.LOGIN_TOKEN,
            value = loginToken,
            maxAge = 60
        )
    }
}