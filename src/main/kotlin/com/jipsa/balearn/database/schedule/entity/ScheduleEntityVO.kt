package com.jipsa.balearn.database.schedule.entity

import com.jipsa.balearn.domain.schedule.ScheduleInfo
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class ScheduleInfoVO(
    @Column(nullable = false)
    val startTime: LocalDateTime,

    @Column(nullable = false)
    val endTime: LocalDateTime,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val topic: String,

    @Column(nullable = false)
    val color: String = "#D3D3D3",
) {
    fun toDomain() = ScheduleInfo(
        startTime = startTime,
        endTime = endTime,
        address = address,
        topic = topic,
        color = color
    )

    companion object {
        fun from(scheduleInfo: ScheduleInfo) = ScheduleInfoVO(
            startTime = scheduleInfo.startTime,
            endTime = scheduleInfo.endTime,
            address = scheduleInfo.address,
            topic = scheduleInfo.topic,
            color = scheduleInfo.color
        )
    }
}