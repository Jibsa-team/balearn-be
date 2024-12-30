package com.jipsa.balearn.database.user.entity

import com.jipsa.balearn.domain.user.AuthProvider
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
)

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
)