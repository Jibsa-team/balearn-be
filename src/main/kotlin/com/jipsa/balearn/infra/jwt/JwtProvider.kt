package com.jipsa.balearn.infra.jwt

import com.jipsa.balearn.domain.user.UserId
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtProvider(
    private val jwtProperties: JwtProperties
) {
    @PostConstruct
    fun init() {
        println("secret: ${jwtProperties.secret}")
    }

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(jwtProperties.secret)
    )

    fun getUserIdFromToken(token: String): UserId {
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        return UserId(claims.subject.toLong())
    }

    fun getExpiration(token: String): Long {
        val expiration = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .expiration

        val now = Date().time
        return (expiration.time - now)
    }


}