package com.jipsa.balearn.infra.redis.config

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.infra.redis.ChatSubscriber
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private var port: Int = 0

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Primary
    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = redisConnectionFactory()

        val stringRedisSerializer = StringRedisSerializer()

        // String 타입 key-value 직렬화 설정
        template.keySerializer = stringRedisSerializer
        template.valueSerializer = stringRedisSerializer

        // Hash Operation 직렬화 설정
        template.hashKeySerializer = stringRedisSerializer
        template.hashValueSerializer = stringRedisSerializer

        template.setEnableTransactionSupport(true)

        return template
    }

    @Bean
    fun anyRedisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        // 최신 방식의 ObjectMapper 설정
        val objectMapper = JsonMapper.builder()
            .addModule(JavaTimeModule()) // LocalDateTime 지원 추가
            .addModule(KotlinModule.Builder().build()) // Kotlin 데이터 클래스 지원
            .build()

        // JSON 직렬화 설정
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = jackson2JsonRedisSerializer

        template.setEnableTransactionSupport(true)
        return template
    }

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        chatSubscriber: ChatSubscriber
    ): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(connectionFactory)
            addMessageListener(chatSubscriber, PatternTopic(BalearnConstants.CHAT_TOPIC))
        }
    }
}
