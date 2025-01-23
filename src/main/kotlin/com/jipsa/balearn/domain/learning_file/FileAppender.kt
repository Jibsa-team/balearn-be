package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.infra.gcs.GcsFileUploader
import org.springframework.stereotype.Component

@Component
class FileAppender(
    private val gcsFileUploader: GcsFileUploader
) {
    fun append(file: File): String {
        return gcsFileUploader.uploadLearningMaterial(file, "learningFile")
    }
}