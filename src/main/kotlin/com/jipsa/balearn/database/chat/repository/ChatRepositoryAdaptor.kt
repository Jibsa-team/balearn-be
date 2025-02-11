package com.jipsa.balearn.database.chat.repository

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import com.fasterxml.jackson.databind.ObjectMapper
import com.jipsa.balearn.database.chat.document.ChatDocument
import com.jipsa.balearn.domain.chat.Chat
import com.jipsa.balearn.domain.chat.ChatRepository
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Repository
class ChatRepositoryAdaptor(
    private val chatEsRepository: ChatEsRepository,
    private val elasticsearchOperations: ElasticsearchOperations,
    private val elasticsearchClient: ElasticsearchClient,
    private val objectMapper: ObjectMapper
) : ChatRepository {
    override fun findByTeamIdAndCursor(teamId: TeamId, cursor: LocalDateTime, pageable: Pageable): List<Chat> {
        return chatEsRepository.findByTeamIdAndCreatedAtBefore(
            teamId.value,
            cursor.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")),
            pageable
        )
            .map { it.toDomain() }
    }

    override fun findByTeamIdBetween(
        teamId: TeamId,
        start: LocalDateTime,
        end: LocalDateTime,
        pageable: Pageable
    ): List<Chat> {
        return chatEsRepository.findChatsInRange(
            teamId.value,
            start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")),
            end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")),
            pageable
        )
            .map { it.toDomain() }
    }

    override fun saveChatsBulk(chats: List<Chat>) {
        if (chats.isEmpty()) return // 빈 리스트는 처리하지 않음.

        val chatDocuments = chats.map {
            ChatDocument.from(it)
        }

        val bulkRequest = BulkRequest.of { b ->
            b.operations(
                chatDocuments.map { chat ->
                    BulkOperation.of { op ->
                        op.index { idx ->
                            idx.index("chat")
                                .document(chat)
                        }
                    }
                }
            )
        }

        val response = elasticsearchClient.bulk(bulkRequest)

        if (response.errors()) {
            response.items().forEachIndexed { index, item ->
                // 만약 어떤 Item에서 에러가 발생했으면, 오류 내용 확인
                val error = item.error()
                if (error != null) {
                    println("Bulk insert error at index $index : $error")
                }
            }
            throw IllegalArgumentException("Elasticsearch Bulk Insert 에러")
        }
    }
}