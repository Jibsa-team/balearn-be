package com.jipsa.balearn.database.user.repository

import com.jipsa.balearn.database.user.entity.UserEntity
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserRepository
import kotlin.jvm.optionals.getOrNull

class UserRepositoryAdaptor (
    private val userJPARepository: UserJPARepository
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

    override fun findById(id: Long): User? {
        return userJPARepository.findById(id).getOrNull()?.toDomain()
    }

}