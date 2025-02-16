package com.jipsa.balearn.infra.redis.config

import com.jipsa.balearn.common.constants.BalearnConstants
import com.jipsa.balearn.infra.redis.ChatSubscriber
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private var port: Int = 6379

    @Value("\${spring.data.redis.password}")
    private lateinit var password: String

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration()
        redisConfig.hostName = host
        redisConfig.port = port
        redisConfig.password = RedisPassword.of(password)

        return LettuceConnectionFactory(redisConfig)
    }

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
