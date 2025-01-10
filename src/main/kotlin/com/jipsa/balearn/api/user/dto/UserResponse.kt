package com.jipsa.balearn.api.user.dto

import com.jipsa.balearn.domain.user.AuthProvider
import com.jipsa.balearn.domain.user.User
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val profileImageUrl: String,
    val provider: AuthProvider,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id.value,
                name = user.userProfile.name,
                email = user.userProfile.email,
                phoneNumber = user.userProfile.phoneNumber,
                profileImageUrl = user.userProfile.profileImageUrl,
                provider = user.userProvider.provider,
                createdAt = user.createdAt,
                modifiedAt = user.modifiedAt
            )
        }
    }
}