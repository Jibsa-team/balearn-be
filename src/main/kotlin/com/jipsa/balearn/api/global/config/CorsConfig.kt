package com.jipsa.balearn.api.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000", "https://balearn.com") // 허용할 도메인 설정
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 설정
        configuration.allowedHeaders = listOf("*") // 허용할 헤더 설정
        configuration.allowCredentials = true // 쿠키 허용 여부
        configuration.maxAge = 3600 // 캐시 시간 설정 (초 단위)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}