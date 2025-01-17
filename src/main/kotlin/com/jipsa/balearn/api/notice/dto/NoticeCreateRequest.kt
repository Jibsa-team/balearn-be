package com.jipsa.balearn.api.notice.dto

data class NoticeCreateRequest(
    val teamId: Long,
    val title: String,
    val detail: String
) {
}