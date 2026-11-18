package com.jipsa.balearn.infra.oauth2

import com.jipsa.balearn.domain.user.TokenAppender
import com.jipsa.balearn.domain.user.UserReader
import com.jipsa.balearn.infra.jwt.JwtGenerator
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomOAuth2LoginSuccessHandler(
    @Value("\${frontend.url}")
    private val frontendUrl: String,
    private val userReader: UserReader,
    private val jwtGenerator: JwtGenerator,
    private val tokenAppender: TokenAppender
) : SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (response.isCommitted) {
            logger.debug("Response has already been committed")
            return
        }

        val email = when (val principal = authentication.principal) {
            is CustomOAuth2UserDetail -> principal.getEmail()
            is OidcUser -> principal.email
            else -> error("Unsupported OAuth principal")
        }
        val user = userReader.readByEmail(email)

        val loginToken = jwtGenerator.generateLoginToken(user)

        tokenAppender.appendLoginToken(response, user.id, loginToken)

        response.sendRedirect(
            "${frontendUrl}/oauth/${
                user.userProvider.provider.name.lowercase()
            }/callback"
        )
    }
}
