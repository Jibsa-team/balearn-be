package com.jipsa.balearn.domain.learning_file

@JvmInline
value class LearningFileId(val value: Long = 0)

data class LearningFIleInfo(
    val name: String,
    val fileUrl: String,
    val size: Long,
    val type: String
)