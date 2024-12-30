package com.jipsa.balearn.database.user.entity

import com.jipsa.balearn.database.global.BaseTimeEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    val id: Long,

    @Embedded
    val userProfile: UserProfileVO,

    @Embedded
    val userProvider: UserProviderVO,

)
    : BaseTimeEntity() {
}