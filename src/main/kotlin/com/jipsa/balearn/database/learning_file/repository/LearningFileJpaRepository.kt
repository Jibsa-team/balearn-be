package com.jipsa.balearn.database.learning_file.repository

import com.jipsa.balearn.database.learning_file.entity.LearningFileEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LearningFileJpaRepository : JpaRepository<LearningFileEntity, Long> {
    fun findByTeam_Id(teamId: Long): List<LearningFileEntity>

    fun deleteByTeam_Id(teamId: Long)

    fun findByTeam_IdOrderByCreatedAtDesc(teamId: Long, pageable: Pageable): List<LearningFileEntity>
}