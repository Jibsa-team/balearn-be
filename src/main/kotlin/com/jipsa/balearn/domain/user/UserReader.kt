package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.user.exception.CustomUserException

class UserReader (
    private val userRepository: UserRepository
) {
    fun read(userId: UserId): User {
        return userRepository.findById(userId.value)?: throw CustomUserException.UserNotFoundException
    }

    fun readByEmail(email: String): User {
        return userRepository.findByEmail(email)?: throw CustomUserException.UserNotFoundException
    }

    fun readByPhoneNumber(phoneNumber: String): User {
        return userRepository.findByPhoneNumber(phoneNumber)?: throw CustomUserException.UserNotFoundException
    }

    fun readAll(): List<User> {
        return userRepository.findAll()
    }
}