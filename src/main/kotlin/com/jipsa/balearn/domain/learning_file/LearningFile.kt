package com.jipsa.balearn.domain.learning_file

import com.jipsa.balearn.domain.global.Base
import com.jipsa.balearn.domain.team.Team
import com.jipsa.balearn.domain.user.UserId
import java.time.LocalDateTime

class LearningFile(
    val id: LearningFileId = LearningFileId(),
    val team: Team,
    private var _learningFileInfo: LearningFIleInfo,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    createdBy: UserId? = null,
    modifiedBy: UserId? = null
) : Base(
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    createdBy = createdBy,
    modifiedBy = modifiedBy
) {
    val learningFileInfo: LearningFIleInfo
        get() = _learningFileInfo

    fun updateLearningFileInfo(learningFileInfo: LearningFIleInfo) {
        _learningFileInfo = learningFileInfo
    }

    fun isCreator(userId: UserId) {
        require(createdBy == userId) { "작성자만 가능합니다." }
    }
}