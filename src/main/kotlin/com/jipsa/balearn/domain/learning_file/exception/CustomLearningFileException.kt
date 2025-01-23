package com.jipsa.balearn.domain.notice.exception

import com.jipsa.balearn.common.exception.CustomException

sealed class CustomLearningFileException(errorCode: LearningFileErrorCode) : CustomException(errorCode) {
    data object LearningFileNotFoundException :
        CustomLearningFileException(LearningFileErrorCode.LEARNING_FILE_NOT_FOUND) {
        private fun readResolve(): Any = LearningFileNotFoundException

        val EXCEPTION: CustomLearningFileException = LearningFileNotFoundException
    }
}