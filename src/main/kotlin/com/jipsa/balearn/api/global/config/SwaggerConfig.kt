package com.jipsa.balearn.api.global.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "BearerAuth" // Security Scheme 이름
        val server: Server = Server().url(System.getenv("BACKEND_URL")) // 서버 URL 설정
        return OpenAPI()
            .components(
                io.swagger.v3.oas.models.Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP) // HTTP 타입
                            .scheme("bearer") // Bearer 토큰 방식
                            .bearerFormat("access token") // JWT 형식 명시
                            .description("access token을 입력하세요")
                            .`in`(SecurityScheme.In.HEADER)
                            .name("Authorization")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName)) // Security Requirement 추가
            .addServersItem(server)
            .info(apiInfo())
    }

    private fun apiInfo(): Info {
        return Info()
            .title("Balearn Swagger")
            .description("Balearn REST API Documentation")
            .version("1.0.0")
    }
}
