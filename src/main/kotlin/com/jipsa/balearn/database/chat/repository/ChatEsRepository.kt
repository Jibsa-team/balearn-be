package com.jipsa.balearn.database.chat.repository

import com.jipsa.balearn.database.chat.document.ChatDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatDocumentRepository : ElasticsearchRepository<ChatDocument, Long> {
    fun findByTeamId(teamId: Long): List<ChatDocument>
}