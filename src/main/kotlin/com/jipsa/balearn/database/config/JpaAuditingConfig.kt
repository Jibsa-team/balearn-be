package com.jipsa.balearn.database.config

import com.jipsa.balearn.domain.user.UserReader
import com.jipsa.balearn.infra.oauth2.CustomOAuth2UserDetail
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@Configuration
class JpaAuditingConfig

@Component
class AuditorAwareImpl(
    private val userReader: UserReader // User 정보 로드에 필요한 의존성 주입
) : AuditorAware<Long> {

    override fun getCurrentAuditor(): Optional<Long> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication != null && authentication.isAuthenticated) {
            // CustomOAuth2UserDetail에서 사용자 이메일 가져오기
            val principal = authentication.principal
            if (principal is CustomOAuth2UserDetail) {
                val user = principal.getUser()
                return Optional.of(
                    user?.id?.value ?: userReader.readByEmail(principal.getEmail()).id.value
                )
            }
        }

        return Optional.empty() // 인증되지 않은 경우 처리
    }
}
