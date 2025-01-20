package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.user.exception.CustomUserException
import org.springframework.stereotype.Component

@Component
class UserUpdater(
    private val userRepository: UserRepository
) {
    fun update(userId: UserId, name: String?, phoneNumber: String?, profileImgUrl: String?): User {
        val user = userRepository.findById(userId.value) ?: throw CustomUserException.UserNotFoundException
        val userProfile = UserProfile(
            name = name ?: user.userProfile.name,
            email = user.userProfile.email,
            phoneNumber = phoneNumber ?: user.userProfile.phoneNumber,
            profileImageUrl = profileImgUrl ?: user.userProfile.profileImageUrl
        )
        user.changeProfile(userProfile)
        return userRepository.save(user)
    }

    fun update(user: User, name: String?, phoneNumber: String?, profileImgUrl: String?): User {
        val userProfile = UserProfile(
            name = name ?: user.userProfile.name,
            email = user.userProfile.email,
            phoneNumber = phoneNumber ?: user.userProfile.phoneNumber,
            profileImageUrl = profileImgUrl ?: user.userProfile.profileImageUrl,
        )
        user.changeProfile(userProfile)
        return userRepository.save(user)
    }
}