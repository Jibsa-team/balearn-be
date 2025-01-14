package com.jipsa.balearn.database.learning_file.entity

import com.jipsa.balearn.database.global.BaseEntity
import com.jipsa.balearn.database.team.entity.TeamEntity
import com.jipsa.balearn.domain.learning_file.LearningFIleId
import com.jipsa.balearn.domain.learning_file.LearningFIleInfo
import com.jipsa.balearn.domain.learning_file.LearningFile
import jakarta.persistence.*

@Entity
@Table(name = "learning_files")
class LearningFileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: TeamEntity,

    @Embedded
    val learningFIleInfo: LearningFIleInfoVO
) : BaseEntity() {
    fun toDomain() = LearningFile(
        id = LearningFIleId(id),
        team = team.toDomain(),
        _learningFileInfo = learningFIleInfo.toDomain()
    )

    companion object {
        fun from(learningFile: LearningFile) = LearningFileEntity(
            id = learningFile.id.value,
            team = TeamEntity.from(learningFile.team),
            learningFIleInfo = LearningFIleInfoVO.from(learningFile.learningFileInfo)
        )
    }
}