package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.global.Base
import java.time.LocalDateTime

class User(
    val id: UserId,
    private var _userProfile: UserProfile,
    val userProvider: UserProvider,
    createdAt: LocalDateTime?,
    modifiedAt: LocalDateTime?
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt
) {

    val userProfile: UserProfile
        get() = _userProfile

    fun changeProfile(userProfile: UserProfile){
        this._userProfile = userProfile
    }

}