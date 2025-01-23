package com.jipsa.balearn.database.user.repository

import com.jipsa.balearn.database.user.entity.UserEntity
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
import com.jipsa.balearn.domain.user.UserRepository
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class UserRepositoryAdaptor(
    private val userJPARepository: UserJpaRepository
) : UserRepository {
    override fun save(user: User): User {
        return userJPARepository.save(UserEntity.from(user)).toDomain()
    }

    override fun findByEmail(email: String): User? {
        return userJPARepository.findByUserProfile_Email(email)?.toDomain()
    }

    override fun findByPhoneNumber(phoneNumber: String): User? {
        return userJPARepository.findByUserProfile_PhoneNumber(phoneNumber)?.toDomain()
    }

    override fun findAll(): List<User> {
        return userJPARepository.findAll().map { it.toDomain() }
    }

    override fun delete(user: User) {
        userJPARepository.delete(UserEntity.from(user))
    }

    override fun deleteById(userId: UserId) {
        userJPARepository.deleteById(userId.value)
    }

    override fun findById(userId: UserId): User? {
        return userJPARepository.findById(userId.value).getOrNull()?.toDomain()
    }

}