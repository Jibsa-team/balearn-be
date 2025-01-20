package com.jipsa.balearn.domain.team_user

import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.infra.gcs.GcsFileUploader
import org.springframework.stereotype.Component

@Component
class TeamUserImageAppender(
    private val gcsFileUploader: GcsFileUploader
) {
    fun append(image: File?): String? {
        return image?.let { gcsFileUploader.uploadImageFile(it, "teamUserProfile") }
    }
}