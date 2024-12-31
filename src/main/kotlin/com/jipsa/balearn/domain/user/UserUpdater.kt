package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.user.exception.CustomUserException

class UserUpdater (
    private val userRepository: UserRepository
) {
    fun update(userId: UserId, name: String?, phoneNumber: String?, profileImgUrl: String?): User {
        val user = userRepository.findById(userId.value) ?: throw CustomUserException.UserNotFoundException
        val userProfile = UserProfile(
                name ?: user.userProfile.name,
                phoneNumber ?: user.userProfile.email,
                profileImgUrl ?: user.userProfile.phoneNumber,
                user.userProfile.profileImageUrl
            )
        user.changeProfile(userProfile)
        return userRepository.save(user)
    }
}