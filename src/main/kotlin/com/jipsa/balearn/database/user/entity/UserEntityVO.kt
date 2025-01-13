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
    val phoneNumber: String?,

    @Column(nullable = false)
    val profileImageUrl: String = "https://blog.kakaocdn.net/dn/bfZZQd/btrua3HciZ9/jSnHklZw9ekuzV8YGLZ9zK/%EC%B9%B4%ED%86%A1%20%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84%20%EC%82%AC%EC%A7%84%28%EC%97%B0%EC%B4%88%EB%A1%9Dver%29.jpg?attach=1&knm=img.jpg",
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