package com.jipsa.balearn.domain.schedule

import java.time.LocalDateTime

@JvmInline
value class ScheduleId(val value: Long = 0)

data class ScheduleInfo(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val address: String,
    val topic: String
)