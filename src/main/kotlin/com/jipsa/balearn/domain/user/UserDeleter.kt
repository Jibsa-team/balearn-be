package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.user.exception.CustomUserException

class UserDeleter (
    private val userRepository: UserRepository
) {
    fun delete(userId: UserId) {
        val user = userRepository.findById(userId.value)?: throw CustomUserException.UserNotFoundException
        userRepository.delete(user)
    }

    fun delete(user: User) {
        userRepository.delete(user)
    }
}