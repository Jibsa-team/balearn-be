package com.jipsa.balearn.database.schedule.entity

import com.jipsa.balearn.domain.schedule.ScheduleInfo
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class ScheduleInfoVO(
    @Column(nullable = false)
    val time: LocalDateTime,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val topic: String
) {
    fun toDomain() = ScheduleInfo(
        time = time,
        address = address,
        topic = topic
    )

    companion object {
        fun from(scheduleInfo: ScheduleInfo) = ScheduleInfoVO(
            time = scheduleInfo.time,
            address = scheduleInfo.address,
            topic = scheduleInfo.topic
        )
    }
}