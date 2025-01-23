package com.jipsa.balearn.domain.user

import com.jipsa.balearn.domain.team_user.TeamUserDeleter
import org.springframework.stereotype.Component

@Component
class UserDeleter(
    private val userRepository: UserRepository,
    private val teamUserDeleter: TeamUserDeleter
) {
    fun delete(userId: UserId) {
        teamUserDeleter.deleteBy(userId)
        userRepository.deleteById(userId)
    }

    fun delete(user: User) {
        teamUserDeleter.deleteBy(user.id)
        userRepository.delete(user)
    }
}