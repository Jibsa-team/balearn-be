package com.jipsa.balearn.api.user

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.user.dto.UserResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
import com.jipsa.balearn.domain.user.UserService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserApi(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun me(
        @CurrentUser user: User
    ): ApiResponse<UserResponse> {
        return ApiResponse.success(UserResponse.from(userService.readUser(user.id)))
    }

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long
    ): ApiResponse<UserResponse> {
        return ApiResponse.success(UserResponse.from(userService.readUser(UserId(userId))))
    }

    @DeleteMapping("/me")
    fun deleteUser(
        @CurrentUser user: User
    ): ApiResponse<Unit> {
        userService.deleteUser(user.id)
        return ApiResponse.success()
    }
}