package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.storage.LearningFileObjectStore
import com.jipsa.balearn.domain.notice.exception.CustomLearningFileException
import org.springframework.stereotype.Component

@Component
class LearningFileDeleter(
    private val learningFileRepository: LearningFileRepository,
    private val objectStores: List<LearningFileObjectStore>
) {
    fun delete(learningFIle: LearningFile) {
        learningFileRepository.delete(learningFIle)
        if (!objectStores.any { it.deleteLearningFile(learningFIle.learningFileInfo.fileUrl) }) {
            throw CustomLearningFileException.LearningFileStorageUnavailableException
        }
    }

    fun delete(learningFIleId: LearningFileId) {
        learningFileRepository.deleteById(learningFIleId)
    }

    fun deleteBy(teamId: TeamId) {
        learningFileRepository.deleteByTeamId(teamId)
    }
}
