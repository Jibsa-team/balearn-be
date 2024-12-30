package com.jipsa.balearn.database.user.entity

import com.jipsa.balearn.database.global.BaseTimeEntity
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
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

) : BaseTimeEntity() {
    fun toDomain(): User {
        return User(
            id = UserId(id),
            _userProfile = userProfile.toDomain(),
            userProvider = userProvider.toDomain(),
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }

    companion object {
        fun from(user: User): UserEntity {
            return UserEntity(
                id = user.id.value,
                userProfile = UserProfileVO.from(user.userProfile),
                userProvider = UserProviderVO.from(user.userProvider)
            )
        }
    }

}