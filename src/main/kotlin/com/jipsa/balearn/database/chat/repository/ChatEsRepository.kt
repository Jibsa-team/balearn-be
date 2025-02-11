package com.jipsa.balearn.database.chat.repository

import com.jipsa.balearn.database.chat.document.ChatDocument
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ChatEsRepository : ElasticsearchRepository<ChatDocument, String> {
    fun findByTeamId(teamId: Long): List<ChatDocument>

    @Query(
        """
{
  "bool": {
    "must": [
      { "term": { "teamId": "?0" } },
      { "range": { "createdAt": { "lt": "?1" } } }
    ]
  }
}
"""
    )
    fun findByTeamIdAndCreatedAtBefore(
        teamId: Long,
        createdAt: String,
        pageable: Pageable
    ): List<ChatDocument>

    @Query(
        """
{
  "bool": {
    "must": [
      { "term": { "teamId": "?0" } },
      { "range": { "createdAt": { "gte": "?1", "lte": "?2" } } }
    ]
  }
}
"""
    )
    fun findChatsInRange(
        teamId: Long,
        start: String,
        end: String,
        pageable: Pageable
    ): List<ChatDocument>
}