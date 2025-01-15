package com.jipsa.balearn.database.mission.entity

import com.jipsa.balearn.domain.mission.MissionInfo
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class MissionInfoVO(
    @Column(nullable = false)
    val detail: String
) {
    fun toDomain() = MissionInfo(detail)

    companion object {
        fun from(missionInfo: MissionInfo) = MissionInfoVO(missionInfo.detail)
    }
}