package com.jipsa.balearn.infra.jwt

import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.infra.jwt.exception.CustomJwtException
import com.jipsa.balearn.infra.redis.repository.RedisRepository
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtValidator(
    private val jwtProperties: JwtProperties,
    private val jwtProvider: JwtProvider,
    private val redisRepository: RedisRepository
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(jwtProperties.secret)
    )

    fun resolveToken(request: HttpServletRequest): String? {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (StringUtils.hasText(token)) {
            if (token.startsWith(BalearnConstants.BEARER)) {
                return token.substring(7).trim { it <= ' ' }
            }
            return token
        }

        return null
    }

    fun validateToken(token: String) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
        } catch (ex: Exception) {
            when (ex) {
                is SecurityException,
                is MalformedJwtException -> throw CustomJwtException.JwtNotValidateException

                is ExpiredJwtException -> throw CustomJwtException.JwtExpriedException
                is UnsupportedJwtException -> throw CustomJwtException.JwtUnsupportedException
                is IllegalArgumentException -> throw CustomJwtException.JwtNotFountException
                else -> throw CustomJwtException.JwtUnknownException
            }
        }
    }

    fun validateRefreshToken(token: String) {
        validateToken(token)
        val userId = jwtProvider.getUserIdFromToken(token)
        val refreshToken = redisRepository.getValue(redisRepository.generateRefreshTokenKey(userId))
            ?: throw CustomJwtException.JwtNotFountException

        if (refreshToken != token) {
            throw CustomJwtException.JwtNotValidateException
        }
    }
}