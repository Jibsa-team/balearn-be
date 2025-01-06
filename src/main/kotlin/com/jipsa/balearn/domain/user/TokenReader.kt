package com.jipsa.balearn.domain.user

import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.common.util.CookieUtil
import com.jipsa.balearn.infra.jwt.JwtValidator
import com.jipsa.balearn.infra.jwt.exception.CustomJwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class TokenReader(
    private val cookieUtil: CookieUtil,
    private val jwtValidator: JwtValidator
) {
    fun read(request: HttpServletRequest): String {
        cookieUtil.getCookieValue(request, BalearnConstants.REFRESH_TOKEN)
            ?.let {
                jwtValidator.validateRefreshToken(it)
                return it
            }
            ?: cookieUtil.getCookieValue(request, BalearnConstants.LOGIN_TOKEN)
                ?.let {
                    jwtValidator.validateLoginToken(it)
                    return it
                }
            ?: throw CustomJwtException.JwtNotFountException
    }
}