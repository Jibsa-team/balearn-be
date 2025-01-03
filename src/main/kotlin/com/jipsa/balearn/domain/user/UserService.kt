package com.jipsa.balearn.domain.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val userAppender: UserAppender,
    private val userReader: UserReader,
    private val userUpdater: UserUpdater,
    private val userDeleter: UserDeleter
) {
    fun appendUser(user: User) {
        userAppender.append(user)
    }

    fun readUser(userId: UserId): User {
        return userReader.read(userId)
    }

    fun updateUser(userId: UserId, name: String?, phoneNumber: String?, profileImgUrl: String?) {
        userUpdater.update(userId, name, phoneNumber, profileImgUrl)
    }

    fun updateUser(user: User, name: String?, phoneNumber: String?, profileImgUrl: String?) {
        userUpdater.update(user, name, phoneNumber, profileImgUrl)
    }

    fun deleteUser(userId: UserId) {
        userDeleter.delete(userId)
    }

    fun deleteUser(user: User) {
        userDeleter.delete(user)
    }
}