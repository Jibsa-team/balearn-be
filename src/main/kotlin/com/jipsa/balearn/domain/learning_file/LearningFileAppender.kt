package com.jipsa.balearn.domain.learning_file

import org.springframework.stereotype.Component

@Component
class LearningFileAppender(
    private val learningFileRepository: LearningFileRepository
) {
    fun append(learningFile: LearningFile): LearningFile {
        return learningFileRepository.save(learningFile)
    }
}