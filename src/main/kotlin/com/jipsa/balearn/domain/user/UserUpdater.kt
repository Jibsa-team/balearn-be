package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.user.exception.CustomUserException
import org.springframework.stereotype.Component

@Component
class UserUpdater(
    private val userRepository: UserRepository
) {
    fun update(user: User, name: String?, phoneNumber: String?, profileImgUrl: String?): User {
        user.changeProfile(
            name = name,
            phoneNumber = phoneNumber,
            profileImgUrl = profileImgUrl
        )
        return userRepository.save(user)
    }
}