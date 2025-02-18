package com.jipsa.balearn.common.dto

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class DateTime(
    val createdAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS),
    val updatedAt: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS),
) {
    companion object {
        fun init(): DateTime {
            return DateTime()
        }
    }
}
