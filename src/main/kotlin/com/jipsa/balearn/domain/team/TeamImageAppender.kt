package com.jipsa.balearn.domain.team

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.storage.MediaUploader
import org.springframework.stereotype.Component

@Component
class TeamImageAppender(
    private val mediaUploader: MediaUploader
) {
    fun append(image: File?): String? {
        return image?.let { mediaUploader.uploadImageFile(it, "teamProfile") }
    }
}
