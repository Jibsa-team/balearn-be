package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.domain.team.TeamId

interface LearningFileRepository {
    fun save(learningFile: LearningFile): LearningFile
    fun findById(learningFIleId: LearningFIleId): LearningFile?
    fun deleteById(learningFIleId: LearningFIleId)
    fun deleteByTeamId(teamId: TeamId)
    fun delete(learningFile: LearningFile)
}