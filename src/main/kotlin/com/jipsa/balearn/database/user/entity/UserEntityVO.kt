package com.jipsa.balearn.database.user.entity

import com.jipsa.balearn.domain.user.AuthProvider
import com.jipsa.balearn.domain.user.UserProfile
import com.jipsa.balearn.domain.user.UserProvider
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated


@Embeddable
data class UserProviderVO(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: AuthProvider,

    @Column(nullable = false)
    val providerId: String
) {
    fun toDomain() = UserProvider(
        provider = provider,
        providerId = providerId
    )

    companion object {
        fun from(userProvider: UserProvider) = UserProviderVO(
            provider = userProvider.provider,
            providerId = userProvider.providerId
        )
    }
}

@Embeddable
data class UserProfileVO(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = true, unique = true)
    val phoneNumber: String,

    @Column(nullable = false)
    val profileImageUrl: String,
) {
    fun toDomain() = UserProfile(
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl
    )

    companion object {
        fun from(userProfile: UserProfile) = UserProfileVO(
            name = userProfile.name,
            email = userProfile.email,
            phoneNumber = userProfile.phoneNumber,
            profileImageUrl = userProfile.profileImageUrl
        )
    }
}