package com.jipsa.balearn.common.security

import com.jipsa.balearn.infra.jwt.JwtFilter
import com.jipsa.balearn.infra.oauth2.CustomOAuth2LoginFailureHandler
import com.jipsa.balearn.infra.oauth2.CustomOAuth2LoginSuccessHandler
import com.jipsa.balearn.infra.oauth2.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customOAuth2LoginSuccessHandler: CustomOAuth2LoginSuccessHandler,
    private val customOAuth2LoginFailureHandler: CustomOAuth2LoginFailureHandler,
    private val jwtFilter: JwtFilter,
    private val exceptionHandlerFilter: ExceptionHandlerFilter,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler
) {

    private val loginUrls = arrayOf("/oauth2", "/login/oauth2/code", "/api/auth/reissue")
    private val permitUrls = arrayOf("/ws/info", "/ws", "/h2-console", "/h2-console/**")

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(*loginUrls).permitAll()
                    .requestMatchers(*permitUrls).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oAuth2LoginConfigurer ->
                oAuth2LoginConfigurer.userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(customOAuth2LoginSuccessHandler)
                    .failureHandler(customOAuth2LoginFailureHandler)
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(exceptionHandlerFilter, JwtFilter::class.java)
            .exceptionHandling {
                it.accessDeniedHandler(customAccessDeniedHandler)
                it.authenticationEntryPoint(customAuthenticationEntryPoint)
            }
            .build()
    }
}