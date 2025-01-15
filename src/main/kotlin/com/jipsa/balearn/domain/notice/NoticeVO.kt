package com.jipsa.balearn.domain.notice


@JvmInline
value class NoticeId(val value: Long = 0)

data class NoticeInfo(
    val title: String,
    val detail: String,
)