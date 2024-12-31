package com.jipsa.balearn.domain.user

class UserAppender (
    private val userRepository: UserRepository
) {
    fun append(user: User): User {
        return userRepository.save(user)
    }
}