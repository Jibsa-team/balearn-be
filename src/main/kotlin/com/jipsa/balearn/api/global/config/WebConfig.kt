package com.jipsa.balearn.api.global.config

import com.jipsa.balearn.api.global.annotation.CurrentUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val currentUserArgumentResolver: CurrentUserArgumentResolver
) : WebMvcConfigurer {
    // CORS 설정
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://dev.balearn.o-r.kr:3000",
                "https://balearn.o-r.kr",
                "https://be.balearn.o-r.kr",
                "https://*.balearn.o-r.kr",
                "https://balearn-fe.vercel.app",
                "https://*.balearn-fe.vercel.app"
            ) // 주로 프론트에서 사용하는 port 번호
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders(
                "*"
            )
            .allowCredentials(true)
            .maxAge(3600)
    }

    // 커스텀 Argument Resolver 추가
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(currentUserArgumentResolver)
    }
}