package com.jipsa.balearn.common.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(JavaTimeModule()) // ✅ LocalDateTime 지원
            registerModule(KotlinModule.Builder().build()) // ✅ Kotlin 지원
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ✅ ISO-8601 형식으로 날짜 저장
        }
    }
}