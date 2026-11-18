package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.storage.MediaUploader
import org.springframework.stereotype.Component

@Component
class FileAppender(
    private val mediaUploader: MediaUploader
) {
    fun append(file: File): String {
        return mediaUploader.uploadLearningMaterial(file, "learningFile")
    }
}
