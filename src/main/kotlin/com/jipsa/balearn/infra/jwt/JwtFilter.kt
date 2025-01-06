package com.jipsa.balearn.infra.jwt

import com.jipsa.balearn.domain.user.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtProvider: JwtProvider,
    private val jwtValidator: JwtValidator,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtValidator.resolveToken(request) ?: return filterChain.doFilter(request, response)
        jwtValidator.validateToken(token)
        val userId = jwtProvider.getUserIdFromToken(token)

        val userDetails = customUserDetailsService.loadUserByUsername(userId.value.toString())

        val authentication = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )

        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

}