package com.jipsa.balearn.domain.user

@JvmInline
value class UserId(val value: Long)

data class UserProfile(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val profileImageUrl: String
)

data class UserProvider(
    val provider: AuthProvider,
    val providerId: String
)

enum class AuthProvider(name: String) {
    GOOGLE("구글"),
    GITHUB("깃허브"),
    KAKAO("카카오"),
    DEFAULT("기본")
}