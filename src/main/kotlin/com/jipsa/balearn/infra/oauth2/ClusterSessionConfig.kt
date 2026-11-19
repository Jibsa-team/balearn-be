package com.jipsa.balearn.infra.oauth2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
import org.springframework.session.web.http.DefaultCookieSerializer

@Configuration
@Profile("cluster")
@EnableRedisHttpSession(redisNamespace = "balearn:session", maxInactiveIntervalInSeconds = 600)
class ClusterSessionConfig {
    @Bean
    fun cookieSerializer() = DefaultCookieSerializer().apply {
        setCookieName("BALEARN_SESSION")
        setUseSecureCookie(true)
        setUseHttpOnlyCookie(true)
        setSameSite("Lax")
    }
}
