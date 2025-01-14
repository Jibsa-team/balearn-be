package com.jipsa.balearn.domain.user

@JvmInline
value class UserId(val value: Long)

data class UserProfile(
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val profileImageUrl: String = "https://cdn.balearn.o-r.kr/profile/default-user.jpg"
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