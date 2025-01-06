package com.jipsa.balearn.infra.oauth2

import com.jipsa.balearn.domain.user.AuthProvider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2UserDetail(
    private val oauth2Attributes: OAuth2Attributes
) : OAuth2User, UserDetails {
    override fun getName(): String {
        return oauth2Attributes.name
    }

    override fun getAttributes(): Map<String, Any> {
        return oauth2Attributes.getAttributes()
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return oauth2Attributes.getAuthorities()
    }

    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return oauth2Attributes.name
    }

    fun getEmail(): String = oauth2Attributes.email
    fun getProvider(): AuthProvider = oauth2Attributes.provider
    fun getSnsId(): String = oauth2Attributes.snsId
    fun getProfileImageUrl(): String = oauth2Attributes.profileImageUrl

}