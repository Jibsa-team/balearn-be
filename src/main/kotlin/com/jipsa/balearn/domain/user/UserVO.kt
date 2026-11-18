package com.jipsa.balearn.domain.user

@JvmInline
value class UserId(val value: Long = 0)

data class UserProfile(
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val profileImageUrl: String = DEFAULT_IMAGE_URL
) {
    companion object {
        const val DEFAULT_IMAGE_URL = "/Avatar.png"
    }
}

data class UserProvider(
    val provider: AuthProvider,
    val providerId: String
)

enum class AuthProvider(name: String) {
    GOOGLE("구글"),
    GITHUB("깃허브"),
    KAKAO("카카오"),
    OIDC("조직 계정"),
    DEFAULT("기본")
}
