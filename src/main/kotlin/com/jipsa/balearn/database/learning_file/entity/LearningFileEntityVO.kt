package com.jipsa.balearn.database.learning_file.entity

import com.jipsa.balearn.domain.learning_file.LearningFIleInfo
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class LearningFIleInfoVO(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val fileUrl: String,

    @Column(nullable = false)
    val size: Double,

    @Column(nullable = false)
    val type: String
) {
    fun toDomain(): LearningFIleInfo {
        return LearningFIleInfo(
            name = name,
            fileUrl = fileUrl,
            size = size,
            type = type
        )
    }

    companion object {
        fun from(learningFIleInfo: LearningFIleInfo): LearningFIleInfoVO {
            return LearningFIleInfoVO(
                name = learningFIleInfo.name,
                fileUrl = learningFIleInfo.fileUrl,
                size = learningFIleInfo.size,
                type = learningFIleInfo.type
            )
        }
    }
}