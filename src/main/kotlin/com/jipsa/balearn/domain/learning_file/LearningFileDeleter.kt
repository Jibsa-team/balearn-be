package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.domain.team.TeamId
import org.springframework.stereotype.Component

@Component
class LearningFileDeleter(
    private val learningFileRepository: LearningFileRepository
) {
    fun delete(learningFIle: LearningFile) {
        learningFileRepository.delete(learningFIle)
    }

    fun delete(learningFIleId: LearningFileId) {
        learningFileRepository.deleteById(learningFIleId)
    }

    fun deleteBy(teamId: TeamId) {
        learningFileRepository.deleteByTeamId(teamId)
    }
}