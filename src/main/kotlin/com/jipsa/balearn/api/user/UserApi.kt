package com.jipsa.balearn.api.user

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.team.dto.TeamCreateRequest
import com.jipsa.balearn.api.user.dto.UserResponse
import com.jipsa.balearn.api.user.dto.UserUpdateRequest
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserId
import com.jipsa.balearn.domain.user.UserService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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

    @PutMapping("/me")
    fun updateUser(
        @RequestPart("data") request: UserUpdateRequest,
        @RequestPart("image", required = false) image: MultipartFile?,
        @CurrentUser user: User
    ): ApiResponse<UserResponse> {
        return ApiResponse.success(
            UserResponse.from(
                userService.updateUser(
                    user = user,
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    image = image?.let { File.from(it) })
            )
        )
    }
}