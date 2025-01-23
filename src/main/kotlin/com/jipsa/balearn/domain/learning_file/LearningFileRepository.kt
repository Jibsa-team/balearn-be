package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.domain.Pageable

interface LearningFileRepository {
    fun save(learningFile: LearningFile): LearningFile
    fun findById(learningFileId: LearningFileId): LearningFile?
    fun deleteById(learningFIleId: LearningFileId)
    fun deleteByTeamId(teamId: TeamId)
    fun delete(learningFile: LearningFile)
    fun findByTeamId(teamId: TeamId, pageable: Pageable): List<LearningFile>
}