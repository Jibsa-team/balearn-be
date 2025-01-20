package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.global.Base
import java.time.LocalDateTime
import javax.swing.text.html.HTML.Tag.U

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