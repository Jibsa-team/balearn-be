package com.jipsa.balearn.api.global.annotation

import com.jipsa.balearn.domain.user.User
import com.jipsa.balearn.domain.user.UserReader
import com.jipsa.balearn.domain.user.exception.CustomUserException
import com.jipsa.balearn.infra.oauth2.CustomOAuth2UserDetail
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CurrentUserArgumentResolver(
    private val userReader: UserReader
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentUser::class.java) &&
                User::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: org.springframework.web.context.request.NativeWebRequest,
        binderFactory: org.springframework.web.bind.support.WebDataBinderFactory?
    ): Any {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.principal is CustomOAuth2UserDetail) {
            val customUserDetail = authentication.principal as CustomOAuth2UserDetail
            val user = customUserDetail.getUser()
            return user ?: userReader.readByEmail(customUserDetail.getEmail())
        }
        throw CustomUserException.UserNotAuthenticatedException
    }
}