package com.jipsa.balearn.infra.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticsearchConfig(
) {

    @Bean
    fun elasticsearchTransport(
        objectMapper: ObjectMapper,
        @Value("\${spring.elasticsearch.uris}") url: String,
        @Value("\${spring.elasticsearch.username:}") username: String,
        @Value("\${spring.elasticsearch.password:}") password: String
    ): ElasticsearchTransport {
        // RestClient를 생성
        val host = HttpHost.create(url)
        val builder = RestClient.builder(host)
        if (username.isNotBlank()) {
            val credentials = BasicCredentialsProvider()
            credentials.setCredentials(AuthScope(host), UsernamePasswordCredentials(username, password))
            builder.setHttpClientConfigCallback { it.setDefaultCredentialsProvider(credentials) }
        }
        val restClient = builder.build()

        // 여기서 Spring Bean으로 등록된 ObjectMapper를 활용
        val jacksonJsonpMapper = JacksonJsonpMapper(objectMapper)

        // 커스텀 Mapper를 포함한 Transport 생성
        return RestClientTransport(restClient, jacksonJsonpMapper)
    }

    @Bean
    fun elasticsearchClient(transport: ElasticsearchTransport): ElasticsearchClient {
        // 위에서 만든 Transport로 ElasticsearchClient 생성
        return ElasticsearchClient(transport)
    }
}
