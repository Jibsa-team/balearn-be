package com.jipsa.balearn.api.learning_file.dto

import com.jipsa.balearn.api.team_user.dto.TeamUserReadResponse
import com.jipsa.balearn.domain.learning_file.LearningFile
import com.jipsa.balearn.domain.team_user.TeamUser
import java.time.LocalDateTime

data class LearningFileResponse(
    val id: Long,
    val name: String,
    val fileUrl: String,
    val size: Long,
    val type: String,
    val createdAt: LocalDateTime?,
    val createdBy: TeamUserReadResponse?,
    val modifiedAt: LocalDateTime?,
    val modifiedBy: TeamUserReadResponse?
) {
    companion object {
        fun from(learningFile: LearningFile, createdBy: TeamUser?, modifiedBy: TeamUser?): LearningFileResponse {
            return LearningFileResponse(
                id = learningFile.id.value,
                name = learningFile.learningFileInfo.name,
                fileUrl = learningFile.learningFileInfo.fileUrl,
                type = learningFile.learningFileInfo.type,
                size = learningFile.learningFileInfo.size,
                createdAt = learningFile.createdAt,
                createdBy = createdBy?.let { TeamUserReadResponse.from(it) },
                modifiedAt = learningFile.modifiedAt,
                modifiedBy = modifiedBy?.let { TeamUserReadResponse.from(it) }
            )
        }
    }
}