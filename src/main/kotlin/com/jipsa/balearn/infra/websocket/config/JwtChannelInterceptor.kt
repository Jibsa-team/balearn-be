package com.jipsa.balearn.infra.websocket.config

import com.jipsa.balearn.domain.user.CustomUserDetailsService
import com.jipsa.balearn.infra.jwt.JwtProvider
import com.jipsa.balearn.infra.jwt.JwtValidator
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class JwtChannelInterceptor(
    private val jwtValidator: JwtValidator,
    private val jwtProvider: JwtProvider,
    private val customUserDetailsService: CustomUserDetailsService
) : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        val sessionAttributes = accessor?.sessionAttributes
        val userDetails = sessionAttributes?.get("user") as? UserDetails

        if (userDetails != null) {
            val authentication = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            )

            SecurityContextHolder.getContext().authentication = authentication
        }

        return message
    }
}