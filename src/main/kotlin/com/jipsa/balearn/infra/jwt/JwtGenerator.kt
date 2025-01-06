package com.jipsa.balearn.infra.jwt

import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKey


@Component
class JwtGenerator(
    private val jwtProperties: JwtProperties,
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(jwtProperties.secret)
    )

    fun generateAccessToken(user: User): String {
        return generateToken(
            claims = mutableMapOf(
            ),
            subject = user.id,
            expirationTime = jwtProperties.accessTokenValidity
        )
    }

    fun generateRefreshToken(user: User): String {
        return generateToken(
            claims = mutableMapOf(
            ),
            subject = user.id,
            expirationTime = jwtProperties.refreshTokenValidity
        )
    }

    fun generateLoginToken(user: User): String {
        return generateToken(
            claims = mutableMapOf(
            ),
            subject = user.id,
            expirationTime = jwtProperties.loginTokenValidity
        )
    }

    private fun generateToken(
        claims: MutableMap<String, Any>,
        subject: UserId,
        expirationTime: Long
    ): String {
        val now = Date()
        val expiration = Date(now.time + expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject.value.toString())
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(secretKey)
            .compact()
    }

}