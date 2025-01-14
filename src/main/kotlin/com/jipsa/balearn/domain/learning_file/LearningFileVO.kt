package com.jipsa.balearn.domain.learning_file

@JvmInline
value class LearningFIleId(val value: Long = 0)

data class LearningFIleInfo(
    val name: String,
    val fileUrl: String,
    val size: Double,
    val type: String
)