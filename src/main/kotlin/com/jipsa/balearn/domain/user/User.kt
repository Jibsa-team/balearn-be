package com.jipsa.balearn.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import com.jipsa.balearn.domain.global.Base
import java.time.LocalDateTime

class User(
    val id: UserId = UserId(),
    private var _userProfile: UserProfile,
    val userProvider: UserProvider,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt
) {

    @get:JsonProperty("_userProfile")
    val userProfile: UserProfile
        get() = _userProfile

    fun changeProfile(name: String?, phoneNumber: String?, profileImgUrl: String?) {
        this._userProfile = UserProfile(
            name = name ?: userProfile.name,
            email = userProfile.email,
            phoneNumber = phoneNumber ?: userProfile.phoneNumber,
            profileImageUrl = profileImgUrl ?: userProfile.profileImageUrl
        )
    }

}