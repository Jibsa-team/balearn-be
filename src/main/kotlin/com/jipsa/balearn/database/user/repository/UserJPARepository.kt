package com.jipsa.balearn.database.user.repository

import com.jipsa.balearn.database.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJPARepository : JpaRepository<UserEntity, Long> {
    fun findByUserProfile_Email(email: String): UserEntity?
    fun findByUserProfile_PhoneNumber(name: String): UserEntity?
}