package com.jipsa.balearn.domain.user

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.infra.gcs.GcsFileUploader
import org.springframework.stereotype.Component

@Component
class UserImageAppender(
    private val gcsFileUploader: GcsFileUploader
) {
    fun append(image: File?): String? {
        return image?.let { gcsFileUploader.uploadImageFile(it, "userProfile") }
    }
}