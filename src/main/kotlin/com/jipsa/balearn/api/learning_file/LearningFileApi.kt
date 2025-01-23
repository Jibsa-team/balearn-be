package com.jipsa.balearn.api.learning_file

import com.grepp.quizy.common.dto.Page
import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.learning_file.dto.LearningFileCreateRequest
import com.jipsa.balearn.api.learning_file.dto.LearningFileResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.common.dto.File
import com.jipsa.balearn.domain.learning_file.LearningFileId
import com.jipsa.balearn.domain.learning_file.LearningFileService
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/file")
class LearningFileApi(
    private val learningFileService: LearningFileService
) {
    @GetMapping("/team/{teamId}")
    fun getTeamLearningFile(
        @PathVariable teamId: Long,
        @CurrentUser user: User,
        page: Page = Page(1, 10)
    ): ApiResponse<List<LearningFileResponse>> {
        return ApiResponse.success(
            learningFileService.readLearningFilesPage(
                teamId = TeamId(teamId),
                user = user,
                pageable = PageRequest.of(page.page?.minus(1) ?: 0, page.size ?: 10)
            )
        )
    }

    @GetMapping("/{fileId}")
    fun getLearningFile(
        @PathVariable fileId: Long,
        @CurrentUser user: User
    ): ApiResponse<LearningFileResponse> {
        return ApiResponse.success(learningFileService.readLearningFile(LearningFileId(fileId), user))
    }

    @PostMapping("/team/{teamId}")
    fun createLearningFile(
        @RequestPart("data") request: LearningFileCreateRequest,
        @RequestPart("file", required = true) file: MultipartFile,
        @PathVariable teamId: Long,
        @CurrentUser user: User
    ): ApiResponse<LearningFileResponse> {
        return ApiResponse.success(
            learningFileService.appendLearningFile(
                name = request.name,
                file = File.from(file),
                user = user,
                teamId = TeamId(teamId)
            )
        )
    }

    @DeleteMapping("/{fileId}")
    fun deleteLearningFile(
        @PathVariable fileId: Long,
        @CurrentUser user: User
    ): ApiResponse<Unit> {
        learningFileService.deleteLearningFile(LearningFileId(fileId), user)
        return ApiResponse.success()
    }
}