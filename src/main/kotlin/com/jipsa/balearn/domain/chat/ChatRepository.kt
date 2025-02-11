package com.jipsa.balearn.domain.chat

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface ChatRepository {
    fun findByTeamIdAndCursor(teamId: TeamId, cursor: LocalDateTime, pageable: Pageable): List<Chat>
    fun saveChatsBulk(chats: List<Chat>)
    fun findByTeamIdBetween(teamId: TeamId, start: LocalDateTime, end: LocalDateTime, pageable: Pageable): List<Chat>
}