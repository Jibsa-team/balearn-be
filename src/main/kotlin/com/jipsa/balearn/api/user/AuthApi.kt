package com.jipsa.balearn.api.user

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.user.dto.TokenResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val userService: UserService
) {

    @PostMapping("/reissue")
    fun reissueToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<TokenResponse> {

        val reissueToken = userService.reissueToken(request, response)
        return ApiResponse.success(TokenResponse.from(reissueToken))
    }

    @PostMapping("/logout")
    fun logout(
        response: HttpServletResponse,
        request: HttpServletRequest,
        @CurrentUser user: User
    ): ApiResponse<Unit> {
        userService.logoutUser(response, request, user.id)
        return ApiResponse.success()
    }
}