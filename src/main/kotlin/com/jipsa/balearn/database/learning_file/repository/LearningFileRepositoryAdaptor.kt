package com.jipsa.balearn.database.learning_file.repository

import com.jipsa.balearn.database.learning_file.entity.LearningFileEntity
import com.jipsa.balearn.domain.learning_file.LearningFIleId
import com.jipsa.balearn.domain.learning_file.LearningFile
import com.jipsa.balearn.domain.team.TeamId
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class LearningFileRepositoryAdaptor(
    private val learningFileJpaRepository: LearningFileJpaRepository
) {
    fun save(learningFile: LearningFile): LearningFile {
        return learningFileJpaRepository.save(LearningFileEntity.from(learningFile)).toDomain()
    }

    fun findById(learningFIleId: LearningFIleId): LearningFile? {
        return learningFileJpaRepository.findById(learningFIleId.value).getOrNull()?.toDomain()
    }

    fun deleteById(learningFIleId: LearningFIleId) {
        learningFileJpaRepository.deleteById(learningFIleId.value)
    }

    fun deleteByTeamId(teamId: TeamId) {
        learningFileJpaRepository.deleteByTeam_Id(teamId.value)
    }
}