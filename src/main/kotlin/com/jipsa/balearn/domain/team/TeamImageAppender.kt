package com.jipsa.balearn.domain.team

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.infra.gcs.GcsFileUploader
import org.springframework.stereotype.Component

@Component
class TeamImageAppender(
    private val gcsFileUploader: GcsFileUploader
) {
    fun append(image: File?): String? {
        return image?.let { gcsFileUploader.uploadImageFile(it) }
    }
}