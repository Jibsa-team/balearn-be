package com.jipsa.balearn.domain.user

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.team_user.TeamUserValidator
import com.jipsa.balearn.infra.jwt.JwtGenerator
import com.jipsa.balearn.infra.jwt.JwtProvider
import com.jipsa.balearn.infra.jwt.JwtValidator
import com.jipsa.balearn.infra.jwt.exception.CustomJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userAppender: UserAppender,
    private val userReader: UserReader,
    private val userUpdater: UserUpdater,
    private val userDeleter: UserDeleter,
    private val jwtGenerator: JwtGenerator,
    private val jwtProvider: JwtProvider,
    private val jwtValidator: JwtValidator,
    private val tokenAppender: TokenAppender,
    private val tokenReader: TokenReader,
    private val tokenDeleter: TokenDeleter,
    private val userImageAppender: UserImageAppender,
    private val teamUserValidator: TeamUserValidator
) {
    @Transactional
    fun appendUser(user: User) {
        userAppender.append(user)
    }

    fun readUser(userId: UserId): User {
        return userReader.read(userId)
    }

    @Transactional
    fun updateUser(user: User, name: String?, phoneNumber: String?, image: File?): User {
        val profileImgUrl = image?.let { userImageAppender.append(image) }
        return userUpdater.update(user, name, phoneNumber, profileImgUrl)
    }

    @Transactional
    fun deleteUser(userId: UserId) {
        teamUserValidator.isExistTeamOwner(userId)
        userDeleter.delete(userId)
    }

    @Transactional
    fun deleteUser(user: User) {
        teamUserValidator.isExistTeamOwner(user.id)
        userDeleter.delete(user)
    }

    fun reissueToken(request: HttpServletRequest, response: HttpServletResponse): ReissueToken {
        val user = userReader.read(jwtProvider.getUserIdFromToken(tokenReader.read(request)))
        val accessToken = jwtGenerator.generateAccessToken(user)
        val refreshToken = jwtGenerator.generateRefreshToken(user)
        val expirationTime = jwtProvider.getExpiration(accessToken)
        tokenAppender.appendRefreshToken(response, user.id, refreshToken)
        tokenDeleter.deleteLoginToken(response, user.id)
        return ReissueToken(accessToken, refreshToken, expirationTime)
    }

    fun logoutUser(response: HttpServletResponse, request: HttpServletRequest, userId: UserId) {
        val accessToken = jwtValidator.resolveToken(request) ?: throw CustomJwtException.JwtNotFountException
        jwtValidator.validateToken(accessToken)
        tokenAppender.appendBlackListToken(response, userId, accessToken, jwtProvider.getExpiration(accessToken))
        tokenDeleter.deleteRefreshToken(response, userId)
        tokenDeleter.deleteLoginToken(response, userId)
    }
}