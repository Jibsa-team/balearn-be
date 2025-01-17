package com.jipsa.balearn.api.notice

import com.jipsa.balearn.api.global.annotation.CurrentUser
import com.jipsa.balearn.api.notice.dto.NoticeCreateRequest
import com.jipsa.balearn.api.notice.dto.NoticeReadResponse
import com.jipsa.balearn.common.api.ApiResponse
import com.jipsa.balearn.domain.notice.NoticeId
import com.jipsa.balearn.domain.notice.NoticeService
import com.jipsa.balearn.domain.team.TeamId
import com.jipsa.balearn.domain.user.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notice")
class NoticeApi(
    private val noticeService: NoticeService
) {

    @PostMapping("/create")
    fun createNotice(
        @CurrentUser user: User,
        @RequestBody request: NoticeCreateRequest
    ): ApiResponse<NoticeReadResponse> {
        return ApiResponse.success(
            NoticeReadResponse.from(
                noticeService.appendNotice(
                    request.title,
                    request.detail,
                    user.id,
                    TeamId(request.teamId)
                )
            )
        )
    }

    @GetMapping("/{noticeId}")
    fun readNotice(
        @CurrentUser user: User,
        @PathVariable noticeId: Long
    ): ApiResponse<NoticeReadResponse> {
        return ApiResponse.success(
            NoticeReadResponse.from(
                noticeService.readNotice(user.id, NoticeId(noticeId))
            )
        )
    }
}