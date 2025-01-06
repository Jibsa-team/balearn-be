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
    fun delete(response: HttpServletResponse, userId: UserId) {
        redisRepository.deleteValue(redisRepository.generateRefreshTokenKey(userId))
        redisRepository.deleteValue(redisRepository.generateLoginTokenKey(userId))
        cookieUtil.deleteCookie(response, BalearnConstants.REFRESH_TOKEN)
        cookieUtil.deleteCookie(response, BalearnConstants.LOGIN_TOKEN)
    }
}