package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.domain.notice.exception.CustomLearningFileException
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class LearningFileReader(
    private val learningFileRepository: LearningFileRepository
) {
    fun read(learningFileId: LearningFileId): LearningFile {
        return learningFileRepository.findById(learningFileId)
            ?: throw CustomLearningFileException.LearningFileNotFoundException
    }

    fun readAllBy(teamId: TeamId, pageable: Pageable): List<LearningFile> {
        return learningFileRepository.findByTeamId(teamId, pageable)
    }
}