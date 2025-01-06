package com.jipsa.balearn.domain.user

import com.jipsa.balearn.infra.oauth2.CustomOAuth2UserDetail
import com.jipsa.balearn.infra.oauth2.OAuth2Attributes
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userReader: UserReader
) : UserDetailsService {
    override fun loadUserByUsername(id: String): UserDetails {
        val user = userReader.read(UserId(id.toLong()))
        return CustomOAuth2UserDetail(
            OAuth2Attributes(
                name = user.userProfile.name,
                email = user.userProfile.email,
                provider = user.userProvider.provider,
                snsId = user.userProvider.providerId,
                profileImageUrl = user.userProfile.profileImageUrl
            )
        )
    }
}