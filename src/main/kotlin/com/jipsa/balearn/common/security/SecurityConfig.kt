package com.jipsa.balearn.common.security

import com.jipsa.balearn.infra.jwt.JwtFilter
import com.jipsa.balearn.infra.oauth2.CustomOAuth2LoginFailureHandler
import com.jipsa.balearn.infra.oauth2.CustomOAuth2LoginSuccessHandler
import com.jipsa.balearn.infra.oauth2.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
    private val permitUrls = arrayOf("/ws", "/ws/**", "/h2-console", "/h2-console/**", "/actuator/*")
    private val swaggerUrls =
        arrayOf("/", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**")

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(*loginUrls).permitAll()
                    .requestMatchers(*permitUrls).permitAll()
                    .requestMatchers(*swaggerUrls).permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { oAuth2LoginConfigurer ->
                oAuth2LoginConfigurer.userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(customOAuth2LoginSuccessHandler)
                    .failureHandler(customOAuth2LoginFailureHandler)
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(exceptionHandlerFilter, jwtFilter::class.java)
            .exceptionHandling {
                it.accessDeniedHandler(customAccessDeniedHandler)
                it.authenticationEntryPoint(customAuthenticationEntryPoint)
            }
            .build()
    }
}