package com.jipsa.balearn.domain.user

import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.common.util.CookieUtil
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class TokenDeleter(
    private val redisRepository: RedisRepository,
    private val cookieUtil: CookieUtil
) {
    fun deleteLoginToken(response: HttpServletResponse, userId: UserId) {
        redisRepository.delete(redisRepository.generateLoginTokenKey(userId))
        cookieUtil.deleteCookie(response, BalearnConstants.LOGIN_TOKEN)
    }

    fun deleteRefreshToken(response: HttpServletResponse, userId: UserId) {
        redisRepository.delete(redisRepository.generateRefreshTokenKey(userId))
        cookieUtil.deleteCookie(response, BalearnConstants.REFRESH_TOKEN)
    }
}