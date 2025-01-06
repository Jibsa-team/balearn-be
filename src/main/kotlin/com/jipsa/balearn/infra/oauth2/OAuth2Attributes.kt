package com.jipsa.balearn.infra.oauth2

import com.jipsa.balearn.domain.user.AuthProvider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

// OAuth2User를 구현하지 않고 별도의 데이터 클래스로 변경
data class OAuth2Attributes(
    val name: String,
    val email: String,
    val provider: AuthProvider,
    val snsId: String,
    val profileImageUrl: String,
    private val attributes: Map<String, Any> = emptyMap()
) {
    fun getAttributes(): Map<String, Any> {
        return attributes
    }

    fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }
}