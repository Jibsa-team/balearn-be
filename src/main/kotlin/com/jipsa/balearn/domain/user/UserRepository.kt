package com.jipsa.balearn.domain.user

interface UserRepository {
    fun save(user: User): User
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun findByPhoneNumber(phoneNumber: String): User?
    fun findAll(): List<User>
    fun delete(user: User)
}